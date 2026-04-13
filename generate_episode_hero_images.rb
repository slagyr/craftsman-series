#!/usr/bin/env ruby

require 'base64'
require 'fileutils'
require 'json'
require 'net/http'
require 'uri'

class XaiImageGenerator
  API_BASE = 'https://api.x.ai/v1'.freeze
  MODEL = 'grok-imagine-image'.freeze
  OUTPUT_DIR = File.expand_path('files/heroes', __dir__)
  CHARACTER_SHEET_BASENAME = 'characters-cast'.freeze
  MAX_REFERENCES = 3

  GLOBAL_STYLE = <<~PROMPT.strip
    Illustrated mid-century retro-futurist pulp science-fiction poster art inspired by the bold theatrical energy of classic 1950s space adventure posters, warm painterly brushwork, dramatic composition, heroic silhouettes, luminous machinery, recognizable recurring characters, humane expressions, believable body language, brass-and-amber palette with teal and crimson accents, no text, no caption, no logo, no watermark.
  PROMPT

  CHARACTER_SHEET_PROMPT = <<~PROMPT.strip
    #{GLOBAL_STYLE}

    Create a cast reference sheet showing the recurring ensemble in clear three-quarter view against a neutral retro-futurist studio backdrop with subtle spacecraft framing. Keep the faces distinct, memorable, and reusable across later images.

    Alphonse: young apprentice programmer in his early twenties, slim build, earnest face, sandy brown hair, slightly rumpled work clothes, intelligent but anxious energy.
    Jerry: experienced journeyman programmer in his mid-thirties, athletic working build, dark brown hair with only a slight hint of gray at the temples, serious expression, practical blue workwear, calm exacting mentor presence.
    Mr. C: master craftsman in his sixties, lean, commanding, silver hair, piercing eyes, severe but thoughtful presence, immaculate dark work jacket.
    Jean: sharp senior engineer in her thirties, poised, dark curly hair, intense analytical gaze, polished utilitarian workwear.
    Jasmine: confident engineer in her late twenties, warm but formidable expression, black hair, precise posture, practical lab attire.
    Avery: thoughtful engineer in his thirties, composed face, short dark hair, observant demeanor, understated work clothes.
    Jasper: exuberant journeyman programmer in his late twenties, wide grin, bright eyes, restless posture, casual utilitarian workwear, eager but patronizing energy.
    Carole: determined project lead in her forties, practical haircut, focused expression, strong presence, clean utilitarian wardrobe.
    Adelaide: bright, ambitious engineer in her late twenties, expressive face, neat hair, energetic and polished presentation.

    Arrange them as a coherent ensemble cast sheet. No text labels.
  PROMPT

  CHARACTERS = {
    alphonse: {
      label: 'Alphonse',
      file: 'alphonse-ref',
      description: 'young apprentice programmer in his early twenties, slim build, earnest face, sandy brown hair, slightly rumpled work clothes, intelligent but anxious energy',
    },
    jerry: {
      label: 'Jerry',
      file: 'jerry-ref',
      description: 'experienced journeyman programmer in his mid-thirties, athletic working build, dark brown hair with only a slight hint of gray at the temples, serious expression, practical blue workwear, calm exacting mentor presence',
    },
    mr_c: {
      label: 'Mr. C',
      file: 'mr-c-ref',
      description: 'master craftsman in his sixties, lean, commanding, silver hair, piercing eyes, severe but thoughtful presence, immaculate dark work jacket',
    },
    jean: {
      label: 'Jean',
      file: 'jean-ref',
      description: 'sharp senior engineer in her thirties, poised, dark curly hair, intense analytical gaze, polished utilitarian workwear',
    },
    jasmine: {
      label: 'Jasmine',
      file: 'jasmine-ref',
      description: 'confident engineer in her late twenties, warm but formidable expression, black hair, precise posture, practical lab attire',
    },
    avery: {
      label: 'Avery',
      file: 'avery-ref',
      description: 'thoughtful engineer in his thirties, composed face, short dark hair, observant demeanor, understated work clothes',
    },
    jasper: {
      label: 'Jasper',
      file: 'jasper-ref',
      description: 'exuberant journeyman programmer in his late twenties, wide grin, bright eyes, restless posture, casual utilitarian workwear, eager but patronizing energy',
    },
    carole: {
      label: 'Carole',
      file: 'carole-ref',
      description: 'determined project lead in her forties, practical haircut, focused expression, strong presence, clean utilitarian wardrobe',
    },
    adelaide: {
      label: 'Adelaide',
      file: 'adelaide-ref',
      description: 'bright, ambitious engineer in her late twenties, expressive face, neat hair, energetic and polished presentation',
    },
  }.freeze

  NAME_PATTERNS = {
    alphonse: /\bAlphonse\b/i,
    jerry: /\bJerry\b/i,
    mr_c: /Mr\.\s*C\b/i,
    jean: /\bJean\b/i,
    jasmine: /\bJasmine\b/i,
    avery: /\bAvery\b/i,
    jasper: /\bJasper\b/i,
    carole: /\bCarole\b/i,
    adelaide: /\bAdelaide\b/i,
  }.freeze

  ARC_DEFAULTS = {
    (1..23) => %i[alphonse jerry],
    (24..43) => %i[alphonse jerry carole],
    (44..51) => %i[adelaide jasmine avery],
    (52..63) => %i[mr_c alphonse jerry],
  }.freeze

  EPISODE_OVERRIDES = {
    '01' => {
      file: 'The Craftsman 01 primes.md',
      alt: 'Alphonse nervously presents his first prime-number program to Jerry in a futuristic software lab.',
      references: %i[alphonse jerry],
      prompt: <<~PROMPT.strip,
        #{GLOBAL_STYLE}

        Use <IMAGE_0> as the exact face and build reference for Alphonse and <IMAGE_1> as the exact face and build reference for Jerry. Show only those two characters on Alphonse's first day in a futuristic software lab. Alphonse sits at a workstation after writing his prime-number program. Jerry stands beside him reviewing the code with stern but measured focus. Alphonse looks eager and worried at the same time. Compose it like a classic mid-century science-fiction poster scene: dramatic angle, iconic silhouettes, glowing control panels, luminous code panes, and a heightened sense of apprenticeship meeting hard critique. Preserve the character identities from the reference images exactly.
      PROMPT
    },
    '02' => {
      file: 'The Craftsman 02 primes.md',
      alt: 'Jerry mentors Alphonse through the first refactoring steps at a shared workstation.',
      references: %i[alphonse jerry],
      prompt: <<~PROMPT.strip,
        #{GLOBAL_STYLE}

        Use <IMAGE_0> as the exact face and build reference for Alphonse and <IMAGE_1> as the exact face and build reference for Jerry. Show only Jerry coaching Alphonse through an early refactoring session. They are shoulder to shoulder at a shared workstation in the same retro-futurist lab. Jerry gestures precisely at the screen while Alphonse listens, slightly embarrassed but deeply engaged. Render it with the bold, poster-like energy of a 1950s space adventure illustration while keeping the emotional focus intimate and human. Preserve the character identities from the reference images exactly.
      PROMPT
    },
    '03' => {
      file: 'The Craftsman 03 primes.md',
      alt: 'Alphonse and Jerry perform a careful whole-program read-through in the lab.',
      references: %i[alphonse jerry],
      prompt: <<~PROMPT.strip,
        #{GLOBAL_STYLE}

        Use <IMAGE_0> as the exact face and build reference for Alphonse and <IMAGE_1> as the exact face and build reference for Jerry. Show only Alphonse and Jerry during a calm whole-program read-through. Jerry is seated or leaning in with analytical focus while Alphonse watches closely, absorbing the lesson about naming and collective code ownership. The workstation displays cleaned-up source code. Use a sweeping classic sci-fi poster composition, but with a quieter emotional tone that suggests growing craftsmanship and trust. Preserve the character identities from the reference images exactly.
      PROMPT
    },
  }.freeze

  def initialize
    @api_key = ENV['GROK_API_KEY'] || ENV['XAI_API_KEY']
    raise 'GROK_API_KEY or XAI_API_KEY must be set' if @api_key.to_s.empty?

    FileUtils.mkdir_p(OUTPUT_DIR)
  end

  def generate_all!
    ensure_character_sheet!
    references = ensure_character_references!
    manifest = {}

    article_files.each do |file|
      episode = episode_definition(file)
      hero_path = existing_hero_path(episode[:number])

      if hero_path
        puts "Keeping existing episode #{episode[:number]} hero image..."
      else
        puts "Generating episode #{episode[:number]} hero image..."
        reference_paths = episode.fetch(:references).map { |key| references.fetch(key) }
        image_bytes = edit_from_references(episode[:prompt], reference_paths)
        hero_path = write_detected_image(File.join(OUTPUT_DIR, "#{episode[:number]}-hero"), image_bytes)
        puts "Saved #{hero_path}"
      end

      manifest[episode[:file]] = {
        src: relative_path(hero_path),
        alt: episode[:alt],
      }
    end

    write_hero_manifest(manifest)
  end

  private

  def ensure_character_sheet!
    existing = Dir[File.join(OUTPUT_DIR, "#{CHARACTER_SHEET_BASENAME}.*")].first
    return existing if existing

    puts 'Generating main character sheet...'
    image_bytes = generate_image(CHARACTER_SHEET_PROMPT)
    path = write_detected_image(File.join(OUTPUT_DIR, CHARACTER_SHEET_BASENAME), image_bytes)
    puts "Saved #{path}"
    path
  end

  def ensure_character_references!
    CHARACTERS.each_with_object({}) do |(key, details), refs|
      existing = Dir[File.join(OUTPUT_DIR, "#{details[:file]}.*")].first
      if existing
        refs[key] = existing
        next
      end

      puts "Generating #{details[:label]} reference portrait..."
      prompt = <<~PROMPT.strip
        #{GLOBAL_STYLE}

        Create a clean single-character reference portrait for #{details[:label]}. #{details[:description].capitalize}. Show the character alone in three-quarter view from about thigh-up against a simple retro-futurist studio backdrop with subtle spacecraft framing. This must function as a stable identity reference for later illustrations, so keep the face clear, distinctive, and front-readable. No text labels.
      PROMPT

      image_bytes = generate_image(prompt, aspect_ratio: '3:4')
      path = write_detected_image(File.join(OUTPUT_DIR, details[:file]), image_bytes)
      refs[key] = path
      puts "Saved #{path}"
    end
  end

  def article_files
    Dir[File.join(__dir__, 'The Craftsman *.md')]
      .reject { |path| File.basename(path) == 'README.md' }
      .sort_by { |path| episode_number_from_file(File.basename(path)) }
  end

  def episode_definition(path)
    file = File.basename(path)
    number = format('%02d', episode_number_from_file(file))
    override = EPISODE_OVERRIDES[number]
    return override.merge(number: number) if override

    text = File.read(path, encoding: 'UTF-8')
    title = article_title(text, file)
    notes = extract_story_notes(text)
    references = select_references(text, episode_number_from_file(file))

    {
      file: file,
      number: number,
      references: references,
      alt: build_alt_text(title, references),
      prompt: build_prompt(title, notes, references, episode_number_from_file(file)),
    }
  end

  def episode_number_from_file(file)
    file[/The Craftsman\s+(\d+)/, 1].to_i
  end

  def article_title(text, file)
    heading = text.lines.find { |line| line.start_with?('# ') }
    (heading || file).sub(/^# /, '').strip
  end

  def extract_story_notes(text)
    paragraphs = text.split(/\n{2,}/).map { |paragraph| paragraph.gsub(/\s+/, ' ').strip }

    selected = paragraphs.reject do |paragraph|
      paragraph.empty? ||
        paragraph.start_with?('#', '##', '```') ||
        paragraph == 'Robert C. Martin' ||
        paragraph.match?(/^\d{1,2}\s+[A-Za-z]+\s+\d{4}$/) ||
        paragraph.start_with?('*This chapter is derived') ||
        paragraph.start_with?('Listing ') ||
        paragraph.match?(/^[A-Za-z0-9_.-]+\.(java|rb|cpp|c|cs)/) ||
        paragraph.include?('The code for this article can be located at:')
    end

    selected.first(3).join(' ')
  end

  def select_references(text, episode_number)
    counts = NAME_PATTERNS.transform_values { |pattern| text.scan(pattern).length }
    explicit = counts.select { |_, count| count.positive? }
                     .sort_by { |key, count| [-count, CHARACTERS.keys.index(key) || 999] }
                     .map(&:first)

    selected = []
    defaults = defaults_for_episode(episode_number)
    selected.concat(defaults.first(1))
    selected.concat(explicit)
    selected.concat(defaults)

    selected.uniq.first(MAX_REFERENCES)
  end

  def defaults_for_episode(episode_number)
    ARC_DEFAULTS.each do |range, defaults|
      return defaults if range.cover?(episode_number)
    end

    %i[alphonse jerry]
  end

  def build_alt_text(title, references)
    labels = references.map { |key| CHARACTERS.fetch(key).fetch(:label) }
    if labels.empty?
      "Hero illustration for #{title}."
    else
      "Hero illustration for #{title}, featuring #{labels.join(', ')}."
    end
  end

  def build_prompt(title, notes, references, episode_number)
    reference_instruction = references.each_with_index.map do |key, index|
      "Use <IMAGE_#{index}> as the exact face and build reference for #{CHARACTERS.fetch(key).fetch(:label)}."
    end.join(' ')

    cast_instruction = if references.length == 1
      "Show the referenced character prominently."
    else
      "Show only the referenced characters, with the most emotionally important one or two most prominent."
    end

    <<~PROMPT.strip
      #{GLOBAL_STYLE}

      #{reference_instruction}
      Create a single 16:9 hero illustration for the episode titled "#{title}".
      Story notes: #{notes}
      #{arc_instruction(episode_number)}
      #{cast_instruction}
      Keep the scene cinematic, emotionally legible, and grounded in software craftsmanship rather than generic fantasy. Preserve the reference identities exactly. No text, no logos, no captions, and no readable UI labels.
    PROMPT
  end

  def arc_instruction(episode_number)
    case episode_number
    when 1..10
      'Emphasize apprenticeship, critique, and the excitement of early programming lessons in a compact futuristic lab.'
    when 11..23
      'Emphasize collaborative engineering work around networked tools, remote systems, and growing design pressure aboard a retro-futurist spacecraft workroom.'
    when 24..43
      'Emphasize team-based product work, acceptance tests, and interpersonal strain in a busy spacecraft engineering department.'
    when 44..51
      'Frame the scene as an intense brown-bag discussion or technical demonstration with bold poster-like composition and visible group dynamics.'
    else
      'Frame the scene as a clean, memorable visual metaphor or discussion scene around software craftsmanship and clean code principles while keeping the recurring cast recognizable.'
    end
  end

  def existing_hero_path(number)
    Dir[File.join(OUTPUT_DIR, "#{number}-hero.*")].first
  end

  def generate_image(prompt, aspect_ratio: '16:9')
    payload = {
      model: MODEL,
      prompt: prompt,
      aspect_ratio: aspect_ratio,
      resolution: '2k',
      response_format: 'b64_json',
    }

    parse_base64_image(post_json('images/generations', payload))
  end

  def edit_from_references(prompt, reference_paths)
    raise 'At least one reference image is required' if reference_paths.empty?

    payload = {
      model: MODEL,
      prompt: prompt,
      aspect_ratio: '16:9',
      resolution: '2k',
      response_format: 'b64_json',
    }

    if reference_paths.length == 1
      payload[:image] = image_reference(reference_paths.first)
    else
      payload[:images] = reference_paths.map { |path| image_reference(path) }
    end

    parse_base64_image(post_json('images/edits', payload))
  end

  def image_reference(path)
    mime_type = mime_type_for(path)
    base64_image = Base64.strict_encode64(File.binread(path))

    {
      url: "data:#{mime_type};base64,#{base64_image}",
      type: 'image_url',
    }
  end

  def post_json(path, payload)
    uri = URI("#{API_BASE}/#{path}")
    http = Net::HTTP.new(uri.host, uri.port)
    http.use_ssl = true
    http.read_timeout = 300

    request = Net::HTTP::Post.new(uri.request_uri)
    request['Authorization'] = "Bearer #{@api_key}"
    request['Content-Type'] = 'application/json'
    request.body = JSON.generate(payload)

    response = http.request(request)
    unless response.is_a?(Net::HTTPSuccess)
      raise "xAI API error #{response.code}: #{response.body}"
    end

    JSON.parse(response.body)
  end

  def parse_base64_image(response_body)
    encoded = response_body.fetch('data').first.fetch('b64_json')
    Base64.decode64(encoded)
  end

  def write_detected_image(base_path, image_bytes)
    extension = detect_extension(image_bytes)
    full_path = "#{base_path}.#{extension}"
    File.binwrite(full_path, image_bytes)
    full_path
  end

  def write_hero_manifest(manifest)
    manifest_path = File.expand_path('hero-images.js', __dir__)
    lines = ["const HERO_IMAGES = {"]

    manifest.sort.each do |file, details|
      lines << "  #{file.inspect}: { src: #{details[:src].inspect}, alt: #{details[:alt].inspect} },"
    end

    lines << '};'
    File.write(manifest_path, lines.join("\n") + "\n")
    puts "Updated #{manifest_path}"
  end

  def relative_path(path)
    path.sub("#{File.expand_path(__dir__)}/", '')
  end

  def detect_extension(image_bytes)
    return 'png' if image_bytes.start_with?("\x89PNG".b)
    return 'jpg' if image_bytes.start_with?("\xFF\xD8\xFF".b)
    return 'webp' if image_bytes.start_with?('RIFF'.b) && image_bytes[8, 4] == 'WEBP'

    'png'
  end

  def mime_type_for(path)
    case File.extname(path).downcase
    when '.png' then 'image/png'
    when '.jpg', '.jpeg' then 'image/jpeg'
    when '.webp' then 'image/webp'
    else 'application/octet-stream'
    end
  end
end

XaiImageGenerator.new.generate_all!
