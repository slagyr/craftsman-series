function articleHref(file) {
  return `index.html?file=${encodeURIComponent(file)}`;
}

function currentArticleFile() {
  const params = new URLSearchParams(window.location.search);
  return params.get("file") || ARTICLES[0].file;
}

function stripLeadingHeading(markdown) {
  const lines = markdown.split("\n");
  if (lines[0] && lines[0].startsWith("# ")) {
    lines.shift();
    while (lines[0] === "") {
      lines.shift();
    }
  }
  return lines.join("\n");
}

function renderMenuItems(filterText) {
  const menuList = document.getElementById("menu-list");
  if (!menuList) {
    return;
  }

  const needle = (filterText || "").trim().toLowerCase();
  const activeFile = currentArticleFile();
  menuList.innerHTML = "";

  ARTICLES.filter((article) => {
    if (!needle) {
      return true;
    }

    return `${article.title} ${article.file} ${articleNumber(article)}`.toLowerCase().includes(needle);
  }).forEach((article) => {
    const link = document.createElement("a");
    link.className = "menu-item";
    if (article.file === activeFile && !window.location.pathname.endsWith("about.html")) {
      link.classList.add("is-active");
    }
    link.href = articleHref(article.file);

    const number = document.createElement("span");
    number.className = "article-num";
    number.textContent = String(articleNumber(article)).padStart(2, "0");

    const title = document.createElement("span");
    title.className = "article-title";
    title.textContent = article.title;

    link.appendChild(number);
    link.appendChild(title);
    menuList.appendChild(link);
  });
}

function initMenu() {
  const button = document.getElementById("menu-button");
  const close = document.getElementById("menu-close");
  const menu = document.getElementById("site-menu");
  const overlay = document.getElementById("menu-overlay");
  const search = document.getElementById("menu-search");

  if (!button || !close || !menu || !overlay) {
    return;
  }

  function openMenu() {
    menu.hidden = false;
    overlay.hidden = false;
    button.setAttribute("aria-expanded", "true");
    document.body.classList.add("menu-open");
    renderMenuItems(search ? search.value : "");
    if (search) {
      search.focus();
    }
  }

  function closeMenu() {
    menu.hidden = true;
    overlay.hidden = true;
    button.setAttribute("aria-expanded", "false");
    document.body.classList.remove("menu-open");
  }

  button.addEventListener("click", openMenu);
  close.addEventListener("click", closeMenu);
  overlay.addEventListener("click", closeMenu);
  document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
      closeMenu();
    }
  });

  if (search) {
    search.addEventListener("input", (event) => {
      renderMenuItems(event.target.value);
    });
  }

  renderMenuItems("");
}

function highlightCodeBlocks() {
  const contentNode = document.getElementById("content");
  if (!contentNode || typeof hljs === "undefined") {
    return;
  }

  contentNode.querySelectorAll("pre code").forEach((block) => {
    hljs.highlightElement(block);
  });
}

function renderHeroImage(article) {
  const wrapper = document.getElementById("hero-media");
  const image = document.getElementById("hero-image");
  if (!wrapper || !image) {
    return;
  }

  const hero = (typeof HERO_IMAGES !== "undefined" && HERO_IMAGES[article.file]) || null;
  if (!hero) {
    wrapper.hidden = true;
    image.removeAttribute("src");
    image.alt = "";
    return;
  }

  image.src = hero.src;
  image.alt = hero.alt || article.title;
  wrapper.hidden = false;
}

function renderNavEntries(navNode, entries) {
  if (!navNode) {
    return;
  }

  navNode.innerHTML = "";

  entries.forEach((entry) => {
    const link = document.createElement("a");
    link.className = "button-secondary";
    link.href = entry.href;
    link.textContent = entry.label;
    navNode.appendChild(link);
  });
}

function renderReaderNav(index) {
  const topNavNode = document.getElementById("reader-nav-top");
  const bottomNavNode = document.getElementById("reader-nav-bottom");
  const topEntries = [];
  const bottomEntries = [];

  if (index > 0) {
    const previousEntry = { label: "Previous Episode", href: articleHref(ARTICLES[index - 1].file) };
    topEntries.push(previousEntry);
    bottomEntries.push(previousEntry);
  }

  topEntries.push({ label: "About", href: "about.html" });

  if (index < ARTICLES.length - 1) {
    const nextEntry = { label: "Next Episode", href: articleHref(ARTICLES[index + 1].file) };
    topEntries.push(nextEntry);
    bottomEntries.push(nextEntry);
  }

  renderNavEntries(topNavNode, topEntries);
  renderNavEntries(bottomNavNode, bottomEntries);
}

async function initReaderPage() {
  initMenu();

  const titleNode = document.getElementById("page-title");
  const metaNode = document.getElementById("page-meta");
  const contentNode = document.getElementById("content");
  const requestedFile = currentArticleFile();
  const index = ARTICLES.findIndex((article) => article.file === requestedFile);

  if (index === -1) {
    titleNode.textContent = "Article not found";
    metaNode.textContent = "The requested file is not part of the published article index.";
    contentNode.innerHTML = '<div class="status">Use the menu to choose an article from the published list.</div>';
    return;
  }

  const article = ARTICLES[index];
  titleNode.textContent = article.title;
  metaNode.textContent = `Episode ${String(articleNumber(article)).padStart(2, "0")} of ${ARTICLES.length}`;
  renderHeroImage(article);
  renderReaderNav(index);

  try {
    const response = await fetch(article.file);
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const markdown = stripLeadingHeading(await response.text());
    const html = marked.parse(markdown, {
      headerIds: true,
      mangle: false,
    });

    contentNode.innerHTML = DOMPurify.sanitize(html, { USE_PROFILES: { html: true } });
    highlightCodeBlocks();
    document.title = `${article.title} | The Craftsman Series`;
  } catch (error) {
    metaNode.textContent = "The article could not be loaded from the repository.";
    contentNode.innerHTML = `<div class="status">${error.message}</div>`;
  }
}
