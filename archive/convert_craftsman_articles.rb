#!/usr/bin/env ruby

require 'cgi'
require 'fileutils'

class CraftsmanConverter
  APPLE_SPACE_PATTERN = %r{<span class="Apple-converted-space">(.*?)</span>}m
  LT_SENTINEL = '__CRAFTSMAN_LT__'
  GT_SENTINEL = '__CRAFTSMAN_GT__'

  def initialize(doc_path)
    @doc_path = File.expand_path(doc_path)
    @html_path = replace_extension(@doc_path, '.html')
    @md_path = replace_extension(@doc_path, '.md')
  end

  def convert
    convert_doc_to_html
    markdown = convert_html_to_markdown(File.read(@html_path, encoding: 'UTF-8'))
    File.write(@md_path, markdown)
    @md_path
  end

  private

  def replace_extension(path, extension)
    path.sub(/\.[^.]+\z/, extension)
  end

  def convert_doc_to_html
    success = system('textutil', '-convert', 'html', '-output', @html_path, @doc_path)
    raise "textutil failed for #{@doc_path}" unless success
  end

  def convert_html_to_markdown(html)
    class_styles = extract_class_styles(html)
    body = html[/<body>(.*)<\/body>/m, 1] || html
    blocks = body.scan(%r{<table\b.*?</table>|<p\b.*?</p>}m)
    rendered = []
    index = 0

    while index < blocks.length
      block = blocks[index]

      if block.start_with?('<table')
        rendered.concat(render_table(block))
        index += 1
        next
      end

      if paragraph_code?(block, class_styles)
        code_blocks = []

        while index < blocks.length && paragraph_code_or_blank?(blocks[index], class_styles)
          code_blocks << blocks[index]
          index += 1
        end

        rendered.concat(render_code_paragraphs(code_blocks))
        next
      end

      rendered.concat(render_paragraph(block))
      index += 1
    end

    cleanup_lines(rendered).join("\n") + "\n"
  end

  def render_paragraph(block)
    klass = block[/class="([^"]+)"/, 1]
    inner = block[/<p\b[^>]*>(.*)<\/p>/m, 1].to_s

    return [''] if blank_paragraph?(inner)

    text = format_inline(inner)
    return [''] if effectively_blank_text?(text)

    case klass
    when 'p1'
      ["# #{normalize_title(text)}", '']
    when 'p2'
      [normalize_metadata_line(text)]
    else
      [text, '']
    end
  end

  def blank_paragraph?(inner)
    stripped = inner
      .gsub(APPLE_SPACE_PATTERN, '')
      .gsub(%r{</?[^>]+>}m, '')
      .gsub(/\s+/, '')

    stripped.empty?
  end

  def render_table(block)
    paragraphs = block.scan(%r{<p\b[^>]*class="([^"]+)"[^>]*>(.*?)</p>}m)
    return [] if paragraphs.empty?

    title = nil
    filename = nil
    code_lines = []

    paragraphs.each do |klass, inner|
      raw = format_code_text(inner)

      next if raw.empty? && code_lines.empty?

      if title.nil? && klass == 'p6'
        title_text = strip_emphasis(format_inline(inner))
        title = title_text unless title_text.empty?
      elsif filename.nil? && looks_like_filename?(raw)
        filename = raw
      else
        code_lines << raw
      end
    end

    code_lines.shift while code_lines.first == ''
    code_lines.pop while code_lines.last == ''

    lines = []
    lines << "## #{title}" if title
    lines << '' if title
    lines << filename if filename
    lines << '' if filename
    lines << "```#{choose_language(filename, code_lines)}"
    lines.concat(code_lines)
    lines << '```'
    lines << ''
    lines
  end

  def render_code_paragraphs(blocks)
    code_lines = blocks.map do |block|
      inner = block[/<p\b[^>]*>(.*)<\/p>/m, 1].to_s
      blank_paragraph?(inner) ? '' : format_code_text(inner)
    end

    code_lines.shift while code_lines.first == ''
    code_lines.pop while code_lines.last == ''

    return [] if code_lines.empty?

    ["```#{choose_language(nil, code_lines)}", *code_lines, '```', '']
  end

  def format_inline(text)
    value = preserve_encoded_angles(text.dup)
    value = expand_apple_spaces(value)
    value.gsub!(%r{<br\s*/?>}i, "\n")
    value.gsub!(%r{<span class="s1">(.*?)</span>}m) { "`#{format_inline(Regexp.last_match(1))}`" }
    value.gsub!(%r{<i>(.*?)</i>}m) { "*#{format_inline(Regexp.last_match(1))}*" }
    value.gsub!(%r{<b>(.*?)</b>}m) { "**#{format_inline(Regexp.last_match(1))}**" }
    value.gsub!(%r{</?span[^>]*>}m, '')
    value.gsub!(%r{</?[^>]+>}m, '')
    value = CGI.unescapeHTML(value)
    value = restore_encoded_angles(value)
    value = normalize_ascii(value)
    value = remove_word_artifacts(value)
    normalize_prose_whitespace(value)
  end

  def format_code_text(text)
    value = text.dup
    value = expand_apple_spaces(value)
    value.gsub!(%r{<br\s*/?>}i, "\n")
    value.gsub!(%r{</?[^>]+>}m, '')
    value = CGI.unescapeHTML(value)
    value = normalize_ascii(value)
    value.lines.map(&:rstrip).join("\n")
  end

  def normalize_ascii(text)
    text
      .tr("\u00A0", ' ')
      .tr("\u2018\u2019\u2032", "'''")
      .tr("\u201C\u201D", '""')
      .gsub(/[\u2013\u2014]/, '-')
      .gsub("\u2026", '...')
  end

  def remove_word_artifacts(text)
    text
      .gsub(/REF\s+[A-Z0-9]+.*?MERGEFORMAT\s+/m, '')
      .gsub(/SHAPE\s+\\\*\s+MERGEFORMAT/, '')
  end

  def normalize_prose_whitespace(text)
    text
      .split("\n")
      .map { |line| line.gsub(/[ \t]+/, ' ').strip }
      .join("\n")
      .strip
  end

  def strip_emphasis(text)
    text.gsub(/\*\*/, '').strip
  end

  def normalize_title(text)
    strip_emphasis(text.split("\n").map(&:strip).join(' ').gsub(/[ \t]+/, ' '))
  end

  def normalize_metadata_line(text)
    if text.start_with?('*') && text.end_with?('*') && text.count('*') > 2
      cleaned = text.delete('*').gsub(/([a-z])([A-Z])/, '\\1 \\2').strip
      "*#{cleaned}*"
    else
      text
    end
  end

  def expand_apple_spaces(text)
    text.gsub(APPLE_SPACE_PATTERN) do
      normalize_ascii(CGI.unescapeHTML(Regexp.last_match(1)))
    end
  end

  def preserve_encoded_angles(text)
    text.gsub('&lt;', LT_SENTINEL).gsub('&gt;', GT_SENTINEL)
  end

  def restore_encoded_angles(text)
    text.gsub(LT_SENTINEL, '<').gsub(GT_SENTINEL, '>')
  end

  def effectively_blank_text?(text)
    text.gsub(/[*_`\s]/, '').empty?
  end

  def extract_class_styles(html)
    styles = {}

    html.scan(/\.([A-Za-z0-9_]+)\s*\{([^}]*)\}/m) do |klass, style|
      styles[klass] = style
    end

    styles
  end

  def paragraph_code?(block, class_styles)
    return false unless block.start_with?('<p')

    klass = block[/class="([^"]+)"/, 1]
    style = class_styles[klass].to_s
    style.include?("'Courier New'")
  end

  def paragraph_code_or_blank?(block, class_styles)
    return false unless block.start_with?('<p')

    inner = block[/<p\b[^>]*>(.*)<\/p>/m, 1].to_s
    paragraph_code?(block, class_styles) || blank_paragraph?(inner)
  end

  def looks_like_filename?(text)
    basename = text.split.first.to_s
    basename.match?(/\A[^\s]+\.(java|cpp|cc|cxx|c|cs|rb)\b/i)
  end

  def choose_language(filename, code_lines)
    explicit = language_for(filename)
    return explicit unless explicit.empty?

    infer_language(code_lines)
  end

  def infer_language(code_lines)
    joined = code_lines.join("\n")

    if joined.match?(/\b(public|private|protected|class|interface|implements|extends|throws|assertEquals|new)\b/) && joined.include?(';')
      'java'
    else
      ''
    end
  end

  def cleanup_lines(lines)
    collapsed = []

    lines.each do |line|
      if line == ''
        collapsed << '' unless collapsed.last == ''
      else
        collapsed << line
      end
    end

    collapsed.pop while collapsed.last == ''
    collapsed
  end

  def language_for(filename)
    basename = filename.to_s.split.first.to_s

    case File.extname(basename).downcase
    when '.java' then 'java'
    when '.cpp', '.cc', '.cxx' then 'cpp'
    when '.c' then 'c'
    when '.cs' then 'csharp'
    when '.rb' then 'ruby'
    else ''
    end
  end
end

if ARGV.empty?
  warn 'usage: convert_craftsman_articles.rb <file.doc> [more.doc ...]'
  exit 1
end

ARGV.each do |doc_path|
  output = CraftsmanConverter.new(doc_path).convert
  puts output
end
