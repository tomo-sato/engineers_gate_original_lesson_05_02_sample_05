// WebAPIを呼び出す関数
function callWebAPI() {
    var button = document.getElementById('btn-more');
    var sinceId = button.getAttribute('data-sinceid');

    // WebAPIのエンドポイントやリクエストを記述
    fetch('http://localhost:8087/api/getMembers?sinceId=' + sinceId)
        .then(response => response.json())
        .then(ret => {
            // WebAPIからのレスポンスを処理
            console.log(ret);

            if (!ret.page_info.has_next) {
                var moreSection = document.getElementById('btn-more-section');
                moreSection.remove();
            } else {
                var button = document.getElementById('btn-more');
                button.setAttribute('data-sinceid', ret.page_info.since_id);
            }

            for (let member of ret.data) {
                // テンプレート要素を取得する
                var templateElement = document.getElementById('template-tr');
                // テンプレートから新しい要素を複製してDOMに追加する
                var clonedTemplate = templateElement.cloneNode(true);

                // データのセットを行う。
                clonedTemplate.querySelector('#member-id').textContent = member.id;
                clonedTemplate.querySelector('#member-name').textContent = member.name;
                clonedTemplate.querySelector('#member-emailAddress').textContent = member.email_address;
                clonedTemplate.querySelector('#member-created').textContent = member.formated_created;
                clonedTemplate.querySelector('#member-updated').textContent = member.formated_updated;

                clonedTemplate.querySelector('#member-name').setAttribute('href', '/member/' + member.id);
                clonedTemplate.querySelector('#member-delete').setAttribute('href', '/member/delete/' + member.id);

                document.getElementById('member-tbody').appendChild(clonedTemplate);
            }

        })
        .catch(error => {
            // エラーハンドリング
            console.error('Error fetching data:', error);
        });
}

