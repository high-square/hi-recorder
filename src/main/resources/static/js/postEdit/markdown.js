'use strict';

let canTyping;

/**
 * 마크다운 기능의 초기화
 */
function initMarkdown(markdownCodeButton, markdownPreviewButton, codeArea, previewArea) {
    canTyping = true;

    markdownCodeButton.classList.add("active");
    markdownCodeButton.classList.add("disabled");

    let callToggleButtonHandler = () => {
        toggleButtonHandler(markdownCodeButton, markdownPreviewButton, codeArea, previewArea);
    };
    markdownCodeButton.addEventListener("click", callToggleButtonHandler, false);
    markdownPreviewButton.addEventListener("click", callToggleButtonHandler, false);

    let callChangeToMarkDown = () => {
        changeToMarkDown(codeArea, previewArea);
    };
    codeArea.addEventListener("input", callChangeToMarkDown);

    codeArea.addEventListener("keypress", handleResizeHeight);

    previewArea.hidden = true;
    codeArea.hidden = false;
}

/**
 * 코드와 프리뷰의 토글 기능 구현
 */
function toggleButtonHandler(markdownCodeButton, markdownPreviewButton, codeArea, previewArea) {
    if (canTyping) {
        markdownPreviewButton.classList.add("active");
        markdownPreviewButton.classList.add("disabled");
        markdownCodeButton.classList.remove("active");
        markdownCodeButton.classList.remove("disabled");
    } else {
        markdownPreviewButton.classList.remove("active");
        markdownPreviewButton.classList.remove("disabled");
        markdownCodeButton.classList.add("active");
        markdownCodeButton.classList.add("disabled");
    }

    codeArea.hidden = canTyping;
    previewArea.hidden = !canTyping;
    canTyping = !canTyping;
}

/**
 * marked 라이브러리를 이용하여 마크다운으로 변환
 */
function changeToMarkDown(codeArea, previewArea) {
    previewArea.innerHTML = marked(codeArea.value, { sanitize: true });
}

/**
 * textArea의 크기를 자동으로 조절하는 메서드
 */
function handleResizeHeight(event) {
    event.target.style.height = 'auto';
    event.target.style.height = event.target.scrollHeight + 'px';
}