document.addEventListener("DOMContentLoaded", function () {
    const reasonRadios = document.querySelectorAll('input[name="reason"]');
    const otherReasonTextarea = document.getElementById("reasonTextarea");
    const submitBtn = document.getElementById("submitBtn");
    const form = document.getElementById("form");
    const status = document.getElementById("status");
    const reportedPostId = document.getElementById("reportedPostId");
    const reportedCommentId = document.getElementById("reportedCommentId");
    const reportedUserEmail = document.getElementById("reportedUserEmail")

    // 초기 설정
    otherReasonTextarea.disabled = true;

    // 라디오 버튼 변경 시 처리
    reasonRadios.forEach((radio) => {
        radio.addEventListener("change", function () {
            if (this.value === "기타") {
                otherReasonTextarea.disabled = false; // 기타 선택 시 활성화
                otherReasonTextarea.focus();
            } else {
                otherReasonTextarea.disabled = true; // 기타 외 선택 시 비활성화
                otherReasonTextarea.value = ""; // 내용 초기화
            }
        });
    });

    // 폼 제출 시 처리
    form.onsubmit = function (e) {
        e.preventDefault();

        const selectedReason = document.querySelector('input[name="reason"]:checked');


        if (selectedReason.value === "기타" && otherReasonTextarea.value.trim() === "") {
            alert("기타 사유를 입력해주세요.");
            return;
        }

        // 상태에 따라 ID 설정
        if (status.value === "게시글") {
            // reportedUserEmail.value =
            reportedPostId.value = ""; // 게시글 ID 설정
            reportedCommentId.value = ""; // 댓글 ID 초기화
        } else if (status.value === "댓글") {
            reportedPostId.value = ""; // 게시글 ID 설정
            reportedCommentId.value = ""; // 댓글 ID 설정
        }

        // AJAX 요청 처리
        const xhr = new XMLHttpRequest();
        const formData = new FormData(form);

        xhr.onreadystatechange = () => {
            if (xhr.readyState !== XMLHttpRequest.DONE)
                return;

            const response = JSON.parse(xhr.responseText);
            if (xhr.status < 200 || xhr.status >= 300) {
                alert(response.message || "오류 발생.");
                return;
            }

            if (response.result === "success") {
                location.href = "./result";
            } else {
                alert(response.message || "오류 발생.");
            }
        };

        xhr.open("POST", location.href);
        xhr.send(formData);
    };
});
