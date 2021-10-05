let loginPage;

document.addEventListener("DOMContentLoaded", function () {
    loginPage = new LoginPage();
});

function LoginPage() {
    this.postLogin = window.location.origin + "/admin/api/login";
}

document.querySelector("#login-form").addEventListener("submit", function (event ) {
    event.preventDefault();
    const id = document.querySelector("#inputId").value;
    const password = document.querySelector("#inputPassword").value;

    fetch(loginPage.postLogin, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'email': id,
            'password': password
        })
    }).then(function (response) {
        if (response.status === 200) {
            response.json().then(data => window.localStorage.setItem('accessToken', 'Bearer ' + data.accessToken));
            location.href = '/admin/members';
        } else {
            alert('아이디/비밀번호가 올바르지 않습니다.');
        }
    });
});
