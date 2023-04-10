'use strict';


const editor = new toastui.Editor({
    el: document.querySelector("#editor"),
    height: "auto",
    initialEditType: "markdown",
    initialValue: document.querySelector("#content").value
});

editor.addHook('addImageBlobHook', addImage);
function addImage(blob, callback) {
    const formData = new FormData();
    formData.append("image", blob);

    fetch(location.origin.toString() + "/image/upload", {
        method: 'POST',
        cache: "no-cache",
        body: formData
    })
        .then((response)=>response.json())
        .then((data)=>{
            callback(location.origin.toString() + data.url);
            processData(data);
        })
}

function processData(data) {
    let form = document.querySelector("#form");

    let input = document.createElement("input");
    input.type = "text";
    input.hidden = true;
    input.name = "images";
    input.value = data.imageId;

    form.appendChild(input);
}

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

function setHeaderImage() {
    const realUpload = document.querySelector('#realUpload');

    realUpload.click();
    realUpload.addEventListener("change", uploadImage);
}

function uploadImage(e) {
    let file = e.currentTarget.files;
    addImage(file[0], renderHeaderImage);
}

function renderHeaderImage(headerImageUrl) {
    document.querySelector("#headImageUrl").value = headerImageUrl.substr(location.origin.length);
    document.querySelector("#headerImage").style.backgroundImage = "url('" + headerImageUrl + "')";
}


