'use strict';

function setMarkdown() {
    let markdownCodeButton = document.getElementById("markdown-code-button");
    let markdownPreviewButton = document.getElementById("markdown-preview-button");
    let codeArea = document.getElementById("codeArea");
    let previewArea = document.getElementById("previewArea");

    document.getElementById("submit").addEventListener("click", (evt) => {
        document.getElementById("form").submit();
    });

    initMarkdown(markdownCodeButton, markdownPreviewButton, codeArea, previewArea);
}

function setTag() {
    let tagBox = document.querySelector('#inner-tag-box');
    let tagInput = document.querySelector('#tag-typing');

    initTag(tagBox, tagInput);
}

window.addEventListener("load", setTag, false);
window.addEventListener("load", setMarkdown, false);


/*
 * input text에서 엔터를 눌렀을 때 자동으로 submit 되는 것을 방지하기 위한 메서드
 */
document.onkeydown = (evt) => {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}


