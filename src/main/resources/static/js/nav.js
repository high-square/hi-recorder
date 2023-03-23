function goLogout() {

    let isLogout = confirm("로그아웃 하시겠습니까?");

    if (isLogout)
        location.href = location.origin + "/logout";
}

function getMyStudies() {
    fetch(location.origin + "/api/myStudy")
        .then((response)=>response.json())
        .then((j)=>setMyStudies(j))
}

function setMyStudies(myStudyResponses) {

    let ul = document.querySelector("#myStudyDropDown");


    for (myStudy of myStudyResponses) {
        let li = document.createElement("li");
        let a = document.createElement("a");
        a.className = "dropdown-item";
        a.href = location.origin + "/study/" + myStudy.studyId;
        a.text = myStudy.studyName;
        li.appendChild(a);
        ul.appendChild(li);
    }

}
