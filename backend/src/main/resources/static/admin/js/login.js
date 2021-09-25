let loginPage;

document.addEventListener("DOMContentLoaded", function () {
    loginPage = new LoginPage();
});

function LoginPage() {
    this.postLogin = window.location.origin + "/admin/api/login";
}

document.querySelector("#login").addEventListener("click", function () {
    let id = document.querySelector("#inputId").value;
    let password = document.querySelector("#inputPassword").value;

    fetch(`${loginPage.postLogin}?id=${id}&password=${password}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    }).then(function (response) {
        if (response.status === 200) {
            location.href = '/admin/members';
        } else {
            alert('관리자만 사용할 수 있습니다.');
        }
    });
});
