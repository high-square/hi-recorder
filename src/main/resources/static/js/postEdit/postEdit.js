'use strict';


const editor = new toastui.Editor({
    el: document.querySelector("#editor"),
    height: "auto",
    initialEditType: "markdown",
    initialValue: document.querySelector("#content").value
});

function setTag() {
    let tagBox = document.querySelector('#inner-tag-box');
    let tagInput = document.querySelector('#tag-typing');

    initTag(tagBox, tagInput);
}

window.addEventListener("load", setTag, false);

/*
 * input text에서 엔터를 눌렀을 때 자동으로 submit 되는 것을 방지하기 위한 메서드
 */
document.onkeydown = (evt) => {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}

function submit() {
    let content = document.querySelector("#content");
    content.textContent = editor.getMarkdown();
    document.querySelector("form").submit();
}


