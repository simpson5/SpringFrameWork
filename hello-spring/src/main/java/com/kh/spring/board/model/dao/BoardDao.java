package com.kh.spring.board.model.dao;

import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.vo.Attachment;
import com.kh.spring.board.model.vo.Board;
import com.kh.spring.board.model.vo.BoardExt;

public interface BoardDao {

	List<Board> selectBoardExt();

	List<Board> selectBoardExt(Map<String, Object> param);

	int countBoardList();

	int insertBoard(BoardExt board);

	int insertAttachment(Attachment attach);

	BoardExt selectOneBoard(int no);

	List<Attachment> selectAttachList(int no);

	BoardExt selectOneBoardCollection(int no);

	Attachment selectOneAttachment(int no);

	List<Board> boardSearch(String key);

}
