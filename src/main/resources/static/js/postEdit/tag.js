let innerBox;
let input;
let tags = [];

/**
 * 태그 기능 초기화
 */
function initTag(tagBox, tagInput) { // 초기화
    innerBox = tagBox;
    input = tagInput;
    // Events
    input.addEventListener('keyup', addTagHandler);
    innerBox.addEventListener('click', removeTagHandler);
    render();
}

/**
 * 태그 추가 로직
 * 엔터를 눌렀을 때 태그를 추가한다.
 */
function addTagHandler(ev){
    if (tags.length >= 5) {
        alert("태그는 최대 5개 등록할 수 있습니다.");
        input.value = "";
    }
    if(ev.key === "Enter"){
        if (getTextLength(input.value) > 20) {
            alert("태그는 최대 20 바이트 입니다.");
        } else if (input.value.trim().length === 0) {
            alert("태그 값을 넣어주세요.");
        } else {
            tags.push(input.value.trim());
        }
        input.value = "";
    }

    render();
}

/**
 * 태그 제거 로직
 * tags에서 동일한 값의 태그를 모두 지운다.
 */
function removeTagHandler(ev) {
    let target = ev.target;
    let textNode = target.previousElementSibling.value.trim();

    if(target.tagName === "SPAN" && target.className === "close"){
        tags = tags.filter((tag)=> {
            return textNode !== tag.trim();
        });
    }

    render();
}

/**
 * 저장된 태그를 화면에 보여준다.
 */
function render() {
    let tagHTML = '';
    tags.forEach(tag => {
        tagHTML += makeTag(tag);
    });
    innerBox.innerHTML = tagHTML;
}

/**
 * 태그 값을 바탕으로 화면에 보이는 html tag를 만든다.
 */
function makeTag(tagName) {
    return `
    <span class="tag">
        <input type="text" class="tag-list" name="tag_list" size=${getTextLength(tagName)} readonly value='${tagName}' />
        <span class="close">&times;</span>
    </span>`;
}


/**
 * 문자열 길이를 byte 단위로 측정하는 메서드
 */
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