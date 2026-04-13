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
  MAX_HERO_ATTEMPTS = 4

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

  EPISODE_VISUALS = {
    '01' => { references: %i[alphonse jerry], focus: 'Alphonse facing his first brutal code review over a prime-number program at a glowing workstation', emotion: 'nervous first-day vulnerability and stern mentorship', composition: 'tight confrontation in a compact retro-futurist lab' },
    '02' => { references: %i[alphonse jerry], focus: 'one clumsy machine split into three clean brass modules as a metaphor for extracted methods', emotion: 'embarrassed learning turning into clarity', composition: 'shared-console mentorship scene with modular machinery' },
    '03' => { references: %i[alphonse jerry], focus: 'a careful whole-program read-through and the birth of shared code ownership', emotion: 'quiet concentration and growing trust', composition: 'two engineers studying one central screen' },
    '04' => { references: %i[alphonse jerry], focus: 'Alphonse and Jerry studying a composite number breaking apart into prime factors like machined components', emotion: 'curious problem solving under close mentorship', composition: 'two-character analytical scene built around an exploded-number mechanical metaphor' },
    '05' => { references: %i[alphonse jerry], focus: 'Jerry deleting working code to teach detachment from code', emotion: 'anger, shock, and humility', composition: 'dramatic erasure at a glowing monitor' },
    '06' => { references: [], focus: 'a literal industrial socket and cable beside a newborn server console', emotion: 'experimental curiosity and invention', composition: 'heroic close-up of plug meeting socket in brass machinery' },
    '07' => { references: [], focus: 'two racing signals colliding at a socket to suggest a race condition', emotion: 'tension and precise engineering', composition: 'dynamic technical metaphor with luminous signal trails' },
    '08' => { references: [], focus: 'paired sockets sending and receiving pulses of light across a dark machine bay', emotion: 'balance and communication', composition: 'symmetrical mechanical exchange' },
    '09' => { references: [], focus: 'a fragile client-server link stretched across a retro control room', emotion: 'connection under load', composition: 'wide network scene anchored by one taut cable' },
    '10' => { references: [], focus: 'frayed dangling threads and cables hanging from a socket server rack', emotion: 'unease and unfinished danger', composition: 'ominous machine still life' },
    '11' => { references: %i[alphonse jerry], focus: 'one thick main conduit feeding many delicate remote lines', emotion: 'disorientation before architectural clarity', composition: 'dominant central trunk line over a control console' },
    '12' => { references: [], focus: 'three ugly jagged lines scarring an otherwise elegant control panel', emotion: 'revulsion at visible design blemish', composition: 'graphic close-up like a repair poster' },
    '13' => { references: [], focus: 'cleanly separated objects floating above a console like physical software modules', emotion: 'structural discovery', composition: 'elegant abstract technical tableau' },
    '14' => { references: [], focus: 'transactions as glowing capsules passing through brass tubes between compartments', emotion: 'flow and coordination', composition: 'message-tube network across a ship interior' },
    '15' => { references: [], focus: 'single responsibility as one tidy machine beside one overloaded monstrosity', emotion: 'comic clarity', composition: 'split comparison poster' },
    '16' => { references: [], focus: 'a precise refactoring strike reshaping a clumsy subsystem into a cleaner mechanism', emotion: 'decisive engineering energy', composition: 'kinetic impact scene in a mechanical workshop' },
    '17' => { references: [], focus: 'mechanical guards and gates protecting a vulnerable code chamber', emotion: 'defense and vigilance', composition: 'gatekeeping tableau with shield-like machinery' },
    '18' => { references: [], focus: 'a tortoise-like service robot carrying a delicate glowing code core', emotion: 'patience and confidence', composition: 'fable-like mid-century adventure poster' },
    '19' => { references: [], focus: 'precision calipers measuring a glowing component to a hair\'s breadth', emotion: 'careful tolerance under pressure', composition: 'instrument-driven close-up' },
    '20' => { references: %i[alphonse jerry], focus: 'a climber sliding down a ladder of tests toward messy machinery', emotion: 'shame, warning, and the cost of backsliding', composition: 'symbolic fall in a lab shaft' },
    '21' => { references: [], focus: 'mismatched panels stitched together like a patchwork quilt of code and metal', emotion: 'repair under strain', composition: 'textural collage of patched machinery' },
    '22' => { references: [], focus: 'a giant faceted insect eye examining a tiny glowing bug in a machine', emotion: 'scrutiny and paranoia', composition: 'surreal inspection close-up' },
    '23' => { references: [], focus: 'frayed cables and torn machine coverings around a once-proud system', emotion: 'weariness before cleanup', composition: 'ragged machine portrait' },
    '24' => { references: [], focus: 'a dosimeter suit on a registration pedestal beneath a red alarm beacon', emotion: 'dread and inevitability at a new project', composition: 'dramatic product-introduction poster' },
    '25' => { references: [], focus: 'a spacesuit identification tag being clipped into place on a suit rack', emotion: 'purposeful first-step momentum', composition: 'crisp registration ritual close-up' },
    '26' => { references: [], focus: 'a requirement table transformed into a literal brass planning table with glowing cells', emotion: 'requirements solidifying into something tangible', composition: 'table-centered metaphor' },
    '27' => { references: %i[carole], focus: 'a glowing highway splitting toward one dominant route under Carole\'s direction', emotion: 'managerial force and deadline pressure', composition: 'roadway metaphor inside a spacecraft control room' },
    '28' => { references: [], focus: 'a sealed vessel cracked open to reveal delicate internal mechanisms', emotion: 'alarm at broken encapsulation', composition: 'glass-and-metal rupture close-up' },
    '29' => { references: [], focus: 'a brass calendar wheel sliding one notch at the center of a suit system', emotion: 'small change with outsized consequences', composition: 'single-object poster with date machinery' },
    '30' => { references: [], focus: 'an intimidating glass-walled woodshed conference room under a harsh lamp', emotion: 'discipline, dread, and correction', composition: 'ominous empty-room scene' },
    '31' => { references: [], focus: 'a shimmering force field powering down around a trapped component', emotion: 'release and relief', composition: 'glowing field collapse with lingering energy' },
    '32' => { references: %i[alphonse avery], focus: 'two reluctant collaborators finally aligning over a shared test page', emotion: 'awkward reconciliation and careful respect', composition: 'paired collaboration scene' },
    '33' => { references: [], focus: 'a maintenance cart and mop clearing spilled code debris from aisle 10', emotion: 'messy relief and progress', composition: 'comic cleanup metaphor in a spacecraft corridor' },
    '34' => { references: [], focus: 'a final locking plate snapping into place on a critical mechanism', emotion: 'hard constraint and certainty', composition: 'single decisive machine moment' },
    '35' => { references: %i[alphonse avery], focus: 'exhausted engineers at the golden quiet end of an impossible day', emotion: 'fatigue and earned calm', composition: 'end-of-shift tableau' },
    '36' => { references: [], focus: 'one mysterious subsystem glowing vivid purple against warm brass machinery', emotion: 'strangeness and emphasis', composition: 'color-driven symbolic still life' },
    '37' => { references: [], focus: 'a suit registration card stamped and diverted into a rejection chute', emotion: 'setback and procedural firmness', composition: 'bureaucratic machine metaphor' },
    '38' => { references: [], focus: 'isolated glass test chambers running side by side without touching', emotion: 'clean separation and rigor', composition: 'modular lab architecture' },
    '39' => { references: [], focus: 'a tangled test rig being reorganized into elegant modular benches', emotion: 'clarity emerging from complexity', composition: 'before-and-after refactoring scene' },
    '40' => { references: [], focus: 'one tiny screw causing a giant machine to seize', emotion: 'frustration at deceptively small problems', composition: 'macro scale mismatch poster' },
    '41' => { references: [], focus: 'a verdict indicator hovering between red and green', emotion: 'ambiguity and compromise', composition: 'binary device held in uneasy tension' },
    '42' => { references: [], focus: 'a maze and chess problem embedded in a glowing control console', emotion: 'cunning and suspicion', composition: 'mental puzzle metaphor' },
    '43' => { references: [], focus: 'a tiny courtroom of punctuation and syntax symbols around one machine part', emotion: 'argumentative precision', composition: 'whimsical technical tribunal' },
    '44' => { references: [], focus: 'a crumpled brown lunch bag on a seminar table spilling angle brackets and type cards', emotion: 'curious informal learning', composition: 'brown-bag still life with generous negative space' },
    '45' => { references: [], focus: 'a brown lunch bag surrounded by looping generic pathways and dependency knots', emotion: 'lively technical debate', composition: 'tabletop concept illustration' },
    '46' => { references: [], focus: 'impossible geometry sketched on a lunch napkin beside coffee rings', emotion: 'intellectual tension', composition: 'napkin-and-chalkboard metaphor' },
    '47' => { references: [], focus: 'two mirrored mechanisms about to misfire in perfect symmetry', emotion: 'comic bad luck and suspense', composition: 'high-symmetry jinx poster' },
    '48' => { references: [], focus: 'a brown lunch bag, gate icon, and glowing finite-state diagram on a lunchroom wall', emotion: 'aha-moment clarity', composition: 'diagram-centered brown-bag scene' },
    '49' => { references: [], focus: 'a modular assembly line with swappable generator parts and one abstract factory hub', emotion: 'inventive abstraction and order', composition: 'industrial poster metaphor' },
    '50' => { references: [], focus: 'a faceted ruby gem casting code-colored light across a workstation', emotion: 'new-language excitement', composition: 'jewel-centered still life' },
    '51' => { references: [], focus: 'a ruby gem traveling through a sequence of ornate doors and nodes', emotion: 'playful traversal and discovery', composition: 'flowing symbolic scene' },
    '52' => { references: [], focus: 'the wrong tools and labels stuffed into a pristine cabinet', emotion: 'mild disgust at inappropriate information', composition: 'cabinet still life' },
    '53' => { references: [], focus: 'a faded note peeling from a modern machine it no longer describes', emotion: 'decay and neglect', composition: 'one machine and one obsolete label' },
    '54' => { references: [], focus: 'duplicate placards repeating the obvious around a simple mechanism', emotion: 'annoyed excess', composition: 'repetition-heavy composition' },
    '55' => { references: [], focus: 'a smeared unreadable instruction obscuring the real controls beneath it', emotion: 'confusion and irritation', composition: 'obscured panel close-up' },
    '56' => { references: [], focus: 'a once-powerful machine draped in ghostly cloth and dead wires', emotion: 'haunting abandonment', composition: 'commented-out ghost machine' },
    '57' => { references: [], focus: 'a long staircase of brittle build stages leading to one distant artifact', emotion: 'friction and tedium', composition: 'endless process metaphor' },
    '58' => { references: [], focus: 'one bold green unlabeled test button at the center of a tidy lab console', emotion: 'simplification and relief', composition: 'minimal heroic object poster with no readable words anywhere' },
    '59' => { references: [], focus: 'one small port overwhelmed by too many cables and argument tags', emotion: 'overload and clutter', composition: 'cable chaos still life' },
    '60' => { references: [], focus: 'a machine awkwardly pushing finished data backward through its input chute', emotion: 'inside-out design confusion', composition: 'reversed-flow mechanism' },
    '61' => { references: [], focus: 'one lever surrounded by too many conflicting flag settings', emotion: 'confused branching and needless toggles', composition: 'overcomplicated control panel' },
    '62' => { references: [], focus: 'a dim corridor branching away from a warm workshop into darkness', emotion: 'temptation and foreboding', composition: 'cinematic corridor scene' },
    '63' => { references: [], focus: 'one precise machined gear floating beside a generalized blueprint constellation', emotion: 'synthesis and perspective', composition: 'reflective final composition' },
  }.freeze

  def initialize
    @api_key = ENV['GROK_API_KEY'] || ENV['XAI_API_KEY']
    raise 'GROK_API_KEY or XAI_API_KEY must be set' if @api_key.to_s.empty?

    FileUtils.mkdir_p(OUTPUT_DIR)
  end

  def generate_all!(requested_numbers = nil)
    ensure_character_sheet!
    references = ensure_character_references!
    manifest = {}

    requested_set = normalize_requested_numbers(requested_numbers)

    article_files.each do |file|
      episode = episode_definition(file)
      hero_path = existing_hero_path(episode[:number])

      if requested_set.nil? || requested_set.include?(episode[:number])
        puts "Generating episode #{episode[:number]} hero image..."
        reference_paths = episode.fetch(:references).map { |key| references.fetch(key) }
        hero_path = render_hero_image(episode[:number], episode[:prompt], reference_paths)
        puts "Saved #{hero_path}"
      else
        raise "Missing existing hero image for episode #{episode[:number]}" unless hero_path
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

  def normalize_requested_numbers(requested_numbers)
    return nil if requested_numbers.nil? || requested_numbers.empty?

    requested_numbers.map { |value| format('%02d', value.to_i) }.to_set
  end

  def episode_definition(path)
    file = File.basename(path)
    number = format('%02d', episode_number_from_file(file))
    visual = EPISODE_VISUALS.fetch(number)
    text = File.read(path, encoding: 'UTF-8')
    title = article_title(text, file)

    {
      file: file,
      number: number,
      references: visual.fetch(:references),
      alt: build_alt_text(title, visual),
      prompt: build_prompt(title, visual),
    }
  end

  def episode_number_from_file(file)
    file[/The Craftsman\s+(\d+)/, 1].to_i
  end

  def article_title(text, file)
    heading = text.lines.find { |line| line.start_with?('# ') }
    (heading || file).sub(/^# /, '').strip
  end

  def build_alt_text(title, visual)
    "Hero illustration for #{title}, centered on #{visual.fetch(:focus).sub(/\A[aA] /, '').sub(/\Aan /, '').sub(/\Athe /, '')}."
  end

  def build_prompt(title, visual)
    references = visual.fetch(:references)
    reference_instruction = references.each_with_index.map do |key, index|
      "Use <IMAGE_#{index}> as the exact face and build reference for #{CHARACTERS.fetch(key).fetch(:label)}."
    end.join(' ')

    cast_instruction = if references.empty?
      'Do not include named recurring characters unless they are tiny, unrecognizable silhouettes; let the object, symbol, or metaphor carry the image.'
    elsif references.length == 1
      'Show the referenced character prominently and avoid adding other named cast members.'
    else
      'Show only the referenced characters, with the most emotionally important one or two most prominent, and avoid introducing extra recurring cast members.'
    end

    <<~PROMPT.strip
      #{GLOBAL_STYLE}

      #{reference_instruction}
      Create a single 16:9 hero illustration for the episode titled "#{title}".
      Focal point: #{visual.fetch(:focus)}.
      Emotional tone: #{visual.fetch(:emotion)}.
      Visual approach: #{visual.fetch(:composition)}.
      #{cast_instruction}
      Keep the scene cinematic, emotionally legible, and grounded in software craftsmanship rather than generic fantasy. Feel free to use metaphor, symbolic objects, architectural space, props, diagrams, or physical machinery if that better communicates the episode than showing people would. No text, no logos, no captions, and no readable UI labels.
    PROMPT
  end

  def existing_hero_path(number)
    Dir[File.join(OUTPUT_DIR, "#{number}-hero.*")].first
  end

  def render_hero_image(number, prompt, reference_paths)
    base_path = File.join(OUTPUT_DIR, "#{number}-hero")

    MAX_HERO_ATTEMPTS.times do |attempt|
      image_bytes = render_candidate_image(prompt, reference_paths)

      path = write_detected_image(base_path, image_bytes)
      width, height = image_dimensions(path)
      return path if width >= height

      warn "Episode #{number} returned portrait image #{width}x#{height}; retrying (#{attempt + 1}/#{MAX_HERO_ATTEMPTS})"
    end

    if reference_paths.length == 1
      warn "Episode #{number} kept returning portrait; falling back to reference-free landscape generation"

      MAX_HERO_ATTEMPTS.times do |attempt|
        image_bytes = generate_image(prompt)
        path = write_detected_image(base_path, image_bytes)
        width, height = image_dimensions(path)
        return path if width >= height

        warn "Episode #{number} fallback also returned portrait #{width}x#{height}; retrying (#{attempt + 1}/#{MAX_HERO_ATTEMPTS})"
      end
    end

    raise "Unable to get a landscape hero image for episode #{number} after #{MAX_HERO_ATTEMPTS} attempts"
  end

  def render_candidate_image(prompt, reference_paths)
    if reference_paths.empty?
      generate_image(prompt)
    else
      edit_from_references(prompt, reference_paths)
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

  def image_dimensions(path)
    bytes = File.binread(path)

    if bytes.start_with?("\x89PNG".b)
      return bytes[16, 8].unpack('NN')
    end

    if bytes.start_with?("\xFF\xD8\xFF".b)
      index = 2
      while index < bytes.bytesize
        break unless bytes.getbyte(index) == 0xFF

        marker = bytes.getbyte(index + 1)
        index += 2
        next if marker == 0xD8 || marker == 0xD9

        length = bytes[index, 2].unpack1('n')
        if [0xC0, 0xC1, 0xC2, 0xC3, 0xC5, 0xC6, 0xC7, 0xC9, 0xCA, 0xCB, 0xCD, 0xCE, 0xCF].include?(marker)
          height, width = bytes[index + 3, 4].unpack('nn')
          return [width, height]
        end

        index += length
      end
    end

    [0, 0]
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

require 'set'

XaiImageGenerator.new.generate_all!(ARGV)
