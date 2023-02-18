'use strict';

let box;
let innerBox;
let input;
let tags = [];
let canTyping;
let markdownCodeButton;
let markdownPreviewButton;
let codeArea;
let previewArea;



// 문자열 길이 변환
let getTextLength = function(str) {
    var len = 0;
    for (var i = 0; i < str.length; i++) {
        if (escape(str.charAt(i)).length == 6) {
            len++;
        }
        len++;
    }
    return len;
}


// ======> 태그 작성
// 태그 추가
function addTagHandler(ev){ // 태그 추가
    if (tags.length >= 5) {
        alert("태그는 최대 5개 등록할 수 있습니다.");
        input.value = "";
    }
    if(ev.key === "Enter"){
        if (getTextLength(input.value) > 20) {
            alert("태그는 최대 20 바이트 입니다.");
        } else if (input.value.trim().length == 0) {
            alert("태그 값을 넣어주세요.");
        } else {
            tags.push(input.value.trim());
        }
        input.value = "";
    }
    render();
}

// 태그 제거
function removeTagHandler(ev) {
    let target = ev.target;
    let textNode = target.previousSibling.value.trim();

    if(target.tagName === "SPAN" && target.className === "close"){
        tags = tags.filter((tag)=> {
            return textNode !== tag.trim();
        });
    }

    render();
}

// 출력부
function render(){
    let tmp = '';
    let template = tag => `<span class="tag"><input type="text" name="tag_list" size=${getTextLength(tag)} style="border: none; background: transparent; color: white" readonly value=${tag}></input><span class="close">&times;</span></span>`;
    tags.forEach(tag=>{
        tmp += template(tag);
    });
    innerBox.innerHTML = tmp;
}

function handleResizeHeight(event) {
    event.target.style.height = 'auto';
    event.target.style.height = event.target.scrollHeight + 'px';
}

function initHandler(){ // 초기화
    innerBox = document.querySelector('#inner-tag-box');
    input = document.querySelector('#tag-typing');

    // Events
    innerBox.addEventListener('click', removeTagHandler);
    input.addEventListener('keyup', addTagHandler);
    render();
}
// ========< 태그 작성

// =======> 토글 마크다운

function setToggle() {
    markdownCodeButton = document.getElementById("markdown-code-button");
    markdownPreviewButton = document.getElementById("markdown-preview-button");
    codeArea = document.getElementById("codeArea");
    previewArea = document.getElementById("previewArea");


    document.getElementById("submit").addEventListener("click", (evt) => {
        document.getElementById("form").submit();
    });

    canTyping=true;

    markdownCodeButton.classList.add("active");
    markdownCodeButton.classList.add("disabled");

    markdownCodeButton.addEventListener("click", toggleButtonHandler, false);
    markdownPreviewButton.addEventListener("click", toggleButtonHandler, false);
    codeArea.addEventListener("keypress", handleResizeHeight);
    codeArea.addEventListener("input", changeToMarkDown);

    previewArea.hidden = true;
    codeArea.hidden = false;
}

function toggleButtonHandler() {
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

    previewArea.hidden = !canTyping;
    codeArea.hidden = canTyping;
    canTyping = !canTyping;
}

function changeToMarkDown() {
    previewArea.innerHTML = marked(codeArea.value, { sanitize: true });
}

// ======< 토글 마크다운


window.addEventListener("load", initHandler, false);
window.addEventListener("load", setToggle, false);

function stopRKey(evt) {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
}

document.onkeydown = stopRKey;

function sleep(num){	//[1/1000초]
    var now = new Date();
    var stop = now.getTime() + num;
    while(true){
        now = new Date();
        if(now.getTime() > stop)return;
    }
}
