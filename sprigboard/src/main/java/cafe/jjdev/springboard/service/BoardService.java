package cafe.jjdev.springboard.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import cafe.jjdev.springboard.mapper.BoardFileMapper;
import cafe.jjdev.springboard.mapper.BoardMapper;
import cafe.jjdev.springboard.vo.Board;
import cafe.jjdev.springboard.vo.BoardRequest;
import cafe.jjdev.springboard.vo.Boardfile;

@Service
@Transactional //중간에 예외가 발생하면 모두 취소
public class BoardService {
	@Autowired
	private BoardMapper boardMapper;
	@Autowired
	private BoardFileMapper boardFileMapper;
	
	//BoardMapper 객체 내 하나의 글을 검색하는 selectBoard메서드 호출
	public Board getBoard(int boardNo) {
		return boardMapper.selectBoard(boardNo);
	}
	//BoardMapper 객체 내 글 목록을 검색하는 selectBoardcount메서드 호출, 페이지당 10행씩 출력, 리턴값 Map영역에 세팅 후 리턴.
	public Map<String, Object> selectBoardList(int currentPage) {
		final int ROW_PER_PAGE = 10;
		int startRow = (currentPage - 1) * ROW_PER_PAGE;
		Map<String, Integer> map = new HashMap<String, Integer>();

		map.put("startRow", startRow);
		map.put("ROW_PER_PAGE", ROW_PER_PAGE);

		int boardCount = boardMapper.selectBoardCount();
		int lastPage = (int)(Math.ceil(boardCount / ROW_PER_PAGE));
		Map<String, Object> returnMap = new HashMap<String, Object>();		
		returnMap.put("list", boardMapper.selectBoardList(map));
		returnMap.put("boardCount", boardCount);
		returnMap.put("lastPage", lastPage);

		return returnMap;
	}
	//BoardMapper 객체 내 전체행을 검색하는 selectBoardCount메서드 호출
	public int getBoardCount() {
		return boardMapper.selectBoardCount();
	}
	//BoardMapper 객체 내 글을 등록하는 insertBoard메서드 호출
	public void addBoard(BoardRequest boardRequest, String path)  {
		/*
		 * 1.boardRequest분리시켜야됨  : board, file ,File정보
		 * 2.board -> boardVo
		 * 3.file 정보 -> boardFileVo
		 * 3.file -> +path경로 를 이용해  물리적장치 저장
		 */
		//1.
		Board board = new Board();
	
		board.setBoardContent(boardRequest.getBoardContent());
		board.setBoardDate(boardRequest.getBoardDate());
		board.setBoardNo(boardRequest.getBoardNo());
		board.setBoardPw(boardRequest.getBoardPw());
		board.setBoardUser(boardRequest.getBoardUser());
		board.setBoardTitle(boardRequest.getBoardTitle());
		//board.getBoardNo()
		boardMapper.insertBoard(board);
		//2.
		List<MultipartFile> files = boardRequest.getFiles();
		for(MultipartFile f : files) {
			//f -> boardfile
			Boardfile boardfile = new Boardfile();
			boardfile.setBoardNo(board.getBoardNo());
			boardfile.setFileSize(f.getSize());
			boardfile.setFileType(f.getContentType());
			
			String OriginalFilename = f.getOriginalFilename();
			// . 뒤부터 저장하라고 잘라준다.
			int i = OriginalFilename.lastIndexOf(".");
			//i다음 줄부터 사용하라고 만들어준다.
			String ext = OriginalFilename.substring(i+1);
			boardfile.setFileExt(ext);
			//랜덤 파일 이름을 만들어준다.
			String fileName = UUID.randomUUID().toString();
			boardfile.setFileName(fileName);
			//전체작업이 롤백 되면 파일삭제작업은 직업해야되 알겟지?? 
		
			//3.파일저장
			
			boardFileMapper.insertBoardFile(boardfile);
			try {
				f.transferTo(new File(path+"/"+fileName+"."+ext));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	//BoardMapper 객체 내 글을 삭제하는 deleteBoard메서드 호출
	public int removeBoard(Board board) {
		return boardMapper.deleteBoard(board);
	}
	//BoardMapper 객체 내 글을 수정하는 updateBoard메서드 호출
	public int modifyBoard(Board board) {
		return boardMapper.updateBoard(board);
	}
}