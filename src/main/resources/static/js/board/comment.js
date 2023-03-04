var newComment = document.querySelector('#newComment');

function countTextLength(content) {
    if (content.length == 0 || content == '') {
        document.querySelector('.textCount').textContent = '0자';
    } else {
        document.querySelector('.textCount').textContent = content.length + '자';
    }
}

newComment.addEventListener("keyup",function (e) {
    let content = e.target.value;
    // 글자수 세기
    countTextLength(content);
    // 글자수 제한

    if (content.length == 201) {
        // 200자 넘으면 알림창 뜨도록
        alert('글자수는 200자까지 입력 가능합니다.');
        // 200자 부터는 타이핑 되지 않도록
        e.target.value=e.target.value.substring(0, 200);
        content=e.target.value;
        countTextLength(content);
    };
});