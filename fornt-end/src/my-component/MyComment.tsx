import {
  Container,
  Typography,
  Paper,
  Box,
  Button,
  TextField,
  Pagination,
  useMediaQuery,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { useNavigate, useLocation, useParams } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function MyComment({ category }: { category: string }) {
  // 댓글 총 페이지 수와 해당 페이지의 댓글
  interface CountAndComments {
    count: number;
    comments: Comment[];
  }

  //댓글
  interface Comment {
    id: number;
    memberName: string;
    content: string;
    createdAt: string;
    updatedAt: string;
    children: Child[];
  }

  //리플
  interface Child {
    memberName: string;
    content: string;
    createdAt: string;
    updatedAt: string;
  }

  //App.tsx 파일에 :keyword와 연동 연동됨
  const { keyword } = useParams<{ keyword: string }>();

  ///App.tsx 파일에 :keyword와 연동 안되면 이걸로 테스트 하세요
  //const keyword = "Heung-Min Son"
  //const keyword = "Manchester United"

  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState("");

  //리플 번호 지정
  const [replyCommentIndex, setReplyCommentIndex] = useState<number | null>(
    null
  );

  //댓글 content
  const [newReply, setNewReply] = useState("");

  //댓글 총 페이지 수
  const [pageCount, setPageCount] = useState<number>(1);
  const navigate = useNavigate();
  const location = useLocation();

  // url 파라미터에서 page 값 가져옴 (없을 경우 기본값 1)
  const [page, setPage] = useState(
    parseInt(new URLSearchParams(location.search).get("page") || "1", 10)
  );
  const [error, setError] = useState<string | null>(null);

  const { isLoggedIn, setIsLoggedIn } = useAuth(); // 로그인 상태 가져오기
  const isMobile = useMediaQuery("(max-width:768px)");

  // 댓글 불러오는 함수 선언
  const fetchComments = async (currentPage: any) => {
    try {
      const response = await fetch(
        `https://dldm.kr/api/${category}/${keyword}/comments?page=${currentPage}`
      );
      if (!response.ok) {
        throw new Error("Failed to fetch comments");
      }
      const commentsData: CountAndComments = await response.json();
      setPageCount(commentsData.count);
      const comments = commentsData.comments;

      for (const comment of comments) {
        comment.createdAt = new Date(comment.createdAt).toLocaleString();
        comment.updatedAt = new Date(comment.updatedAt).toLocaleString();
        for (const child of comment.children) {
          child.createdAt = new Date(child.createdAt).toLocaleString();
          child.updatedAt = new Date(child.updatedAt).toLocaleString();
        }
      }
      setComments(comments);
    } catch (error: any) {
      setError(error.message);
    }
  };

  // 댓글 불러오는 함수 사용
  useEffect(() => {
    if (keyword) {
      fetchComments(page);
    }
  }, [keyword, page]);

  // 댓글 content 바뀌는 걸 실시간으로 적용
  const handleCommentChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewComment(event.target.value);
  };

  // 댓글 등록 함수
  const handleCommentSubmit = async () => {
    // 로그인을 위해 localStorage에서 accessToken 가져옴
    const sssAccessToken = localStorage.getItem("sssAccessToken");

    if (newComment.trim()) {
      //const newCommentData  = { content: newComment.trim(), replies: [] };
      const requestData = { content: newComment.trim() };

      try {
        const response = await fetch(
          `https://dldm.kr/api/${category}/${keyword}/comments`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + sssAccessToken || "",
            },
            body: JSON.stringify(requestData),
          }
        );

        if (!response.ok) {
          //throw new Error('Failed to submit comment');
          const resultText = await response.text();
          alert(resultText);
        } else {
          const responseData = await response.json();
          responseData.createdAt = new Date(
            responseData.createdAt
          ).toLocaleString();
          responseData.updatedAt = new Date(
            responseData.updatedAt
          ).toLocaleString();

          // Update local state after successful submission
          setComments([...comments, responseData]);

          // 댓글 비우기
          setNewComment("");
        }
      } catch (error: any) {
        setError(error.message);
      }
    }
  };

  // 리플 변수 실시간 할당
  const handleReplyChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewReply(event.target.value);
  };

  // 리플 달기 on off
  const handleReplyToggle = (index: number) => {
    setReplyCommentIndex(replyCommentIndex === index ? null : index);
  };

  // 리플 등록 함수
  const handleReplySubmit = async (parentId: number, index: number) => {
    // 로그인을 위해 localStorage에서 accessToken 가져옴
    const sssAccessToken = localStorage.getItem("sssAccessToken");
    const isLoggedIn = !!sssAccessToken; // Check if user is logged in

    if (newReply.trim()) {
      //const newCommentData  = { content: newComment.trim(), replies: [] };
      const requestData = { content: newReply.trim(), parentId: parentId };

      try {
        const response = await fetch(
          `https://dldm.kr/api/${category}/${keyword}/comments`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: "Bearer " + sssAccessToken || "",
            },
            body: JSON.stringify(requestData),
          }
        );

        if (!response.ok) {
          //throw new Error('Failed to submit comment');
          const resultText = await response.text();
          alert(resultText);
        } else {
          const responseData = await response.json();

          // Update local state after successful submission
          if (newReply.trim()) {
            const updatedComments = [...comments];

            updatedComments[index].children.push({
              content: newReply.trim(),
              memberName: responseData.memberName,
              createdAt: new Date(responseData.createdAt).toLocaleString(),
              updatedAt: new Date(responseData.updatedAt).toLocaleString(),
            });

            setComments(updatedComments);
            setNewReply("");
            setReplyCommentIndex(null);
          }
        }
      } catch (error: any) {
        setError(error.message);
      }
    }
  };

  // 댓글 페이지 변경 이벤트
  const handlePageChange = (
    event: React.ChangeEvent<unknown>,
    value: number
  ) => {
    setPage(value);
    navigate(`?page=${value}`);
  };

  return (
    <>
      {/* 댓글 목록, 페에징, 댓글 등록*/}
      <Container style={{ marginTop: "20px", textAlign: "left" }}>
        <Typography variant="h6">Comments</Typography>
        <Paper
          style={{ padding: "16px", marginTop: "10px", marginBottom: "10px" }}
        >
          {comments.length > 0 ? (
            comments.map((comment, index) => (
              <Box
                key={index}
                style={{ padding: "8px", borderBottom: "1px solid #ddd" }}
              >
                <Box>
                  <Box>
                    {/* 새로운 댓글이 한 페이지 범위를 넘으면 "끝 페이지 추가됨" 표시 */}
                    {index > 9 && (
                      <Typography
                        color="secondary"
                        style={{ fontStyle: "italic", marginBottom: "10px" }}
                      >
                        끝 페이지에 추가됨
                      </Typography>
                    )}

                    <Typography component="span" sx={{ mr: 2 }}>
                      {comment.memberName}
                    </Typography>
                    <Typography>{comment.content}</Typography>
                    <Typography component="span" sx={{ mr: 2 }}>
                      {comment.createdAt}
                    </Typography>
                    {isMobile && <br />}
                    <Typography component="span">
                      {comment.updatedAt}
                    </Typography>
                  </Box>
                  {/* 리플  */}
                  {comment.children.map((child, replyIndex) => (
                    <Box
                      key={replyIndex}
                      style={{ marginLeft: "20px", padding: "4px" }}
                    >
                      <Typography component="span" sx={{ mr: 2 }}>
                        {child.memberName}
                      </Typography>
                      <Typography
                        style={{ fontStyle: "italic", color: "#555" }}
                      >
                        {child.content}
                      </Typography>
                      <Typography component="span" sx={{ mr: 2 }}>
                        {child.createdAt}
                      </Typography>
                      {isMobile && <br />}
                      <Typography component="span">
                        {child.updatedAt}
                      </Typography>
                    </Box>
                  ))}
                </Box>

                {/* 리플 입력 활성화 버튼 */}
                {isLoggedIn && (
                  <Button
                    variant="outlined"
                    color="primary"
                    onClick={() => handleReplyToggle(index)}
                    style={{ marginTop: "5px" }}
                  >
                    {replyCommentIndex === index ? "Cancel" : "Reply"}
                  </Button>
                )}

                {/* 리플 입력 */}
                {replyCommentIndex === index && (
                  <Box style={{ marginTop: "5px" }}>
                    <TextField
                      label="Reply"
                      variant="outlined"
                      size="small"
                      fullWidth
                      multiline
                      minRows={2}
                      value={newReply}
                      onChange={handleReplyChange}
                    />
                    <Button
                      variant="contained"
                      color="primary"
                      onClick={() => handleReplySubmit(comment.id, index)}
                      style={{ marginTop: "5px" }}
                    >
                      Submit
                    </Button>
                  </Box>
                )}
              </Box>
            ))
          ) : (
            <Typography>댓글이 아직 없습니다.</Typography>
          )}
        </Paper>

        {/* 페이징 버튼*/}
        {pageCount > 0 && (
          <Pagination
            onChange={handlePageChange}
            page={page}
            count={pageCount}
            color="primary"
            showFirstButton={true}
            showLastButton={true}
            sx={{ display: "flex", justifyContent: "center" }}
          />
        )}

        {/* 댓글 등록 창*/}
        <TextField
          label="댓글을 남겨보세요"
          variant="outlined"
          fullWidth
          value={newComment}
          multiline
          minRows={2}
          onChange={handleCommentChange}
          style={{ marginTop: "10px" }}
        />

        {/* 댓글 submit*/}
        {isLoggedIn && (
          <Button
            variant="contained"
            color="primary"
            onClick={handleCommentSubmit}
            style={{ marginTop: "10px" }}
          >
            등록
          </Button>
        )}
      </Container>
    </>
  );
}
