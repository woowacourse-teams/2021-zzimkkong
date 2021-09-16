let indexPage;

document.addEventListener("DOMContentLoaded", function () {
    indexPage = new IndexPage();
    indexPage.initIndexPage();
});

function IndexPage() {
    this.postLogin = window.location.origin + "/api/login";
}

document.querySelector(".btn").addEventListener("click", function () {
    let id = document.querySelector("#inputId").value;
    let password = document.querySelector("#inputPassword").value;
    fetch(indexPage.postLogin + '?id=' + id + '&password=' + password, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    }).then(function (response) {
        if (response.ok) {
            return response.json().then(data => location.href = data);
        } else {
            alert('관리자만 사용할 수 있습니다.');
        }
    });
});
