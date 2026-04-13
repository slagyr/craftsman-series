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

  EPISODES = {
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

  def generate_pilot!
    ensure_character_sheet!
    references = ensure_character_references!
    manifest = {}

    EPISODES.each do |episode_number, episode|
      puts "Generating episode #{episode_number} hero image..."
      reference_paths = episode.fetch(:references).map { |key| references.fetch(key) }
      image_bytes = edit_from_references(episode[:prompt], reference_paths)
      path = write_detected_image(File.join(OUTPUT_DIR, "#{episode_number}-hero"), image_bytes)
      manifest[episode[:file]] = {
        src: relative_path(path),
        alt: episode[:alt],
      }
      puts "Saved #{path}"
    end

    write_hero_manifest(manifest)
  end

  private

  def ensure_character_sheet!
    puts 'Generating main character sheet...'
    image_bytes = generate_image(CHARACTER_SHEET_PROMPT)
    path = write_detected_image(File.join(OUTPUT_DIR, CHARACTER_SHEET_BASENAME), image_bytes)
    puts "Saved #{path}"
    path
  end

  def ensure_character_references!
    CHARACTERS.each_with_object({}) do |(key, details), refs|
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

XaiImageGenerator.new.generate_pilot!
