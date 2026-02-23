let memberPage;
let page;
let currentSearch = '';
let currentGroup = null;
let availableGroups = [];

document.addEventListener("DOMContentLoaded", function () {
    memberPage = new MemberPage();
    memberPage.initMemberPage();

    // Enter 키 검색 지원
    document.getElementById('search-input').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchMembers();
        }
    });
});

function MemberPage() {
    this.getMembers = window.location.origin + "/admin/api/members";
}

// 그룹 목록 조회
function fetchGroups() {
    return fetch(window.location.origin + '/api/groups')
        .then(response => response.json())
        .then(groups => {
            availableGroups = groups;
            renderGroupFilters();
        })
        .catch(error => {
            console.error('그룹 목록 조회 실패:', error);
            availableGroups = [];
        });
}

// 그룹 필터 버튼 동적 생성
function renderGroupFilters() {
    const container = document.getElementById('group-filters');
    let html = '<button class="filter-btn active" onclick="filterByGroup(\'ALL\')">전체</button>';

    availableGroups.forEach(group => {
        html += `<button class="filter-btn" onclick="filterByGroup('${group.name}')">${group.displayName}</button>`;
    });

    container.innerHTML = html;
}

function getMembers(pageNumber, isNewSearch = false) {
    if (isNewSearch) {
        document.querySelector(".members-row").innerHTML = '';
        page = 0;
    }

    page = pageNumber;
    let url = memberPage.getMembers + "?page=" + pageNumber;

    if (currentSearch) {
        url += "&search=" + encodeURIComponent(currentSearch);
    }
    if (currentGroup) {
        url += "&group=" + currentGroup;
    }

    fetch(url, {
        headers: {
            Authorization: window.localStorage.getItem('accessToken')
        }
    }).then(function (response) {
        if (response.status === 401) {
            handleUnauthorized();
            return;
        } else {
            response.json().then(data => {
                const memberList = document.querySelector(".members-row");
                memberList.innerHTML += data.members.map(member => {
                    const groupInfo = availableGroups.find(g => g.name === member.group) || { displayName: member.group, color: '#6B7280' };
                    const groupOptions = availableGroups.map(g =>
                        `<option value="${g.name}" ${member.group === g.name ? 'selected' : ''}>${g.displayName}</option>`
                    ).join('');

                    // OAuth Provider 표시
                    let oauthDisplay = '<span style="color: var(--color-gray);">일반</span>';
                    if (member.oauthProvider === 'GOOGLE') {
                        oauthDisplay = '<i class="bi bi-google" style="color: #DB4437; font-size: 1.2rem;" title="Google"></i> <span style="color: var(--color-gray);">Google</span>';
                    } else if (member.oauthProvider === 'GITHUB') {
                        oauthDisplay = '<i class="bi bi-github" style="color: #333; font-size: 1.2rem;" title="GitHub"></i> <span style="color: var(--color-gray);">GitHub</span>';
                    }

                    return `<tr class="member">
                        <th scope="row">${member.id}</th>
                        <td>${member.email}</td>
                        <td>${member.userName}</td>
                        <td>${member.organization || '-'}</td>
                        <td>${oauthDisplay}</td>
                        <td><span class="group-badge" style="background-color: ${groupInfo.color}">${groupInfo.displayName}</span></td>
                        <td>
                            <select class="form-select form-select-sm"
                                    onchange="updateMemberGroup(${member.id}, '${member.group}', this.value)">
                                ${groupOptions}
                            </select>
                        </td>
                    </tr>`;
                }).join("");
            });
        }
    });
}

MemberPage.prototype.initMemberPage = function () {
    page = 0;

    // URL에서 search 파라미터 확인
    const urlParams = new URLSearchParams(window.location.search);
    const searchParam = urlParams.get('search');

    if (searchParam) {
        currentSearch = searchParam;
        document.getElementById('search-input').value = searchParam;
    }

    // 그룹 목록 먼저 로드한 후 멤버 목록 조회
    fetchGroups().then(() => {
        getMembers(page);
    });
}

document.addEventListener('scroll', () => {
    if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight) {
        getMembers(page + 1);
    }
})

function updateMemberGroup(memberId, currentGroup, newGroup) {
    if (currentGroup === newGroup) {
        return;
    }

    const groupInfo = availableGroups.find(g => g.name === newGroup);
    const groupDisplayName = groupInfo ? groupInfo.displayName : newGroup;

    if (!confirm(`정말 그룹을 ${groupDisplayName}로 변경하시겠습니까?`)) {
        // 취소 시 select를 원래 값으로 되돌리기
        event.target.value = currentGroup;
        return;
    }

    fetch(window.location.origin + '/admin/api/members/' + memberId + '/group', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Authorization: window.localStorage.getItem('accessToken')
        },
        body: JSON.stringify({ group: newGroup })
    })
    .then(response => {
        if (response.status === 401) {
            handleUnauthorized();
            return;
        }
        if (response.ok) {
            alert('그룹이 변경되었습니다.');
            // 페이지 새로고침
            location.reload();
        } else {
            alert('그룹 변경에 실패했습니다.');
            event.target.value = currentGroup;
        }
    })
    .catch(error => {
        alert('오류가 발생했습니다: ' + error.message);
        event.target.value = currentGroup;
    });
}

function searchMembers() {
    const searchInput = document.getElementById('search-input');
    currentSearch = searchInput.value.trim();
    getMembers(0, true);
}

function clearSearch() {
    document.getElementById('search-input').value = '';
    currentSearch = '';
    currentGroup = null;

    // 모든 필터 버튼 비활성화
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelectorAll('.filter-btn')[0].classList.add('active'); // 전체 버튼 활성화

    getMembers(0, true);
}

function filterByGroup(group) {
    currentGroup = group === 'ALL' ? null : group;

    // 버튼 활성화 상태 변경
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');

    getMembers(0, true);
}
