package com.finalproject.team4.shouldbe.service;

import com.finalproject.team4.shouldbe.vo.BoardVO;
import com.finalproject.team4.shouldbe.vo.PagingVO;

import java.util.List;

public interface BoardService {
    public int boardInsert(BoardVO vo);

    public List<BoardVO> boardPageList(PagingVO pVO);

    public int totalRecord(PagingVO pVO);

    public BoardVO boardSelect(int post_id);

    public void viewCount(int post_id);

    public int boardUpdate(BoardVO vo);

    public int boardDelete(int post_id);
    public int increaseLike(int post_id, String user_id);
    public int getLikeStatus(int post_id, String user_id);
    public int getLike(int post_id);

    public int report(int no, String userId);
    public int getReport(int no, String userId);
}
