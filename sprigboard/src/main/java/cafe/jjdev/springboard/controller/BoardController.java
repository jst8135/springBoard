package cafe.jjdev.springboard.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cafe.jjdev.springboard.service.BoardService;
import cafe.jjdev.springboard.vo.Board;
import cafe.jjdev.springboard.vo.BoardRequest;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;

    // 글 수정 폼 요청(비밀번호 입력 폼) - 리스트에서 수정버튼 클릭시 넘어온 boardNo값을 받아 BoardService객체 내 getBoard메서드 호출시 입력, 포워드방식으로 boardUpdate화면 출력.
    @GetMapping(value="/boardUpdate")
    public String boardUpdate(@RequestParam(value="boardNo", required=true) int boardNo, Model model) {
    	System.out.println(boardNo + "<-boardNo.boardUpdate");
    	Board board = boardService.getBoard(boardNo);
     	model.addAttribute("board", board);
         return "boardUpdate";
    }
    // 글 수정 요청 - 수정폼에서 넘어온 데이터들을 받아 Board객체 내부에 세팅. BoardService객체 내 modifyBoard메서드 호출, 리다이렉트 방식으로 boardList로 이동.
    @PostMapping(value="/boardUpdate")
    public String boardUpdate(Board board) {
    	boardService.modifyBoard(board);

    	return "redirect:/boardList";
    }

    // 글 삭제 폼 요청(비밀번호 입력 폼) - 리스트에서 삭제 클릭시 넘어온 boardNo값을 받아 BoardService객체 내 getBoard메서드 호출시 입력, 리턴된 Board객체의 주소를 Model영역에 세팅, 포워드방식으로 boardRemove화면 출력.
    @GetMapping(value="/boardRemove")
    public String boardRemove(@RequestParam(value="boardNo", required=true) int boardNo, Model model) {
        Board board = boardService.getBoard(boardNo);
    	model.addAttribute("board", board);
        return "boardRemove";
    }
    // 글 삭제 요청 - 삭제화면에 입력된 데이터들을 받아 Board객체 내 세팅, BoardService객체 내 removeBoard메서드 호출, 리다이렉트 방식으로 boardList로 이동.
    @PostMapping(value="/boardRemove")
    public String boardRemove(Board board) {
    	boardService.removeBoard(board);  
        return "redirect:/boardList";       
    }

    // 글 상세 내용 요청 - 리스트에서 제목 클릭시 넘어온 boardNo값을 받아 BoardService객체 내 getBoard메서드 호출시 입력, 리턴된 Board객체의 주소를 Model영역에 세팅, 포워드방식으로 boardView화면 출력.
    @GetMapping(value="/boardView")
    public String boardView(Model model, int boardNo) {
        System.out.println(boardNo + "<-boardNo.boardView");
    	Board board = boardService.getBoard(boardNo);
    	model.addAttribute("board", board);
        return "/boardView";
    }

    // 리스트 요청 - 화면에서 현재페이지를 받아 BoardService객체 내 selectBoardList메서도 호출시 입력. Map영역에 세팅되어온 데이터들을 받아 Model영역에 세팅. 포워드방식으로 boardList화면 출력.
    @GetMapping(value="/boardList")
    public String boardList(Model model, @RequestParam(value="currentPage", required=false, defaultValue="1") int currentPage) {
    	Map<String, Object> resultMap = boardService.selectBoardList(currentPage);
       	int lastPage = (int) resultMap.get("lastPage");
       	int boardCount = (int) resultMap.get("boardCount");
       	model.addAttribute("resultMap", resultMap);
    	model.addAttribute("currentPage", currentPage);
    	model.addAttribute("lastPage", lastPage);
    	model.addAttribute("boardCount", boardCount);
        return "/boardList";
    }


    // 입력(액션) 요청 - 입력폼에 입력된 데이터를 받아 Board객체에 세팅. BoardService객체 내 addBoard메서드를 호출. 리다이렉트 방식으로 boardList로 이동.
   
    @PostMapping(value="/boardAdd")
    // 타입  (@RequestParam(value="file")MultipartFile[] file) <--? vo페키지에 담아서 board로 사용하면됨
    public String boardAdd(BoardRequest boardRequest, HttpServletRequest request){
    	//spring이 board를 채워주려함=command객체, 필드명=인풋타임명 =>setter
        System.out.println(boardRequest);
		/* Service
		 * 1.board 안에 fileList분해하여 DB에들어갈수있는 형태로 작업해야한다.
		 * 2.파일 저장: 파일경로...
		 */
        //졸라어렵다! 이경로!
        String path = request.getSession().getServletContext().getRealPath("./");
        
        try {
			boardService.addBoard(boardRequest,path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        return "redirect:/boardList"; // 글입력후 "/boardList"로 리다이렉트(재요청)
    }
    // 입력페이지 요청 - 웹브라우저 경로에 boardAdd를 입력하면 boardAdd.html화면이 출력.
    @GetMapping(value="/boardAdd")
    public String boardAdd() {
        System.out.println("boardAdd 폼 요청");
        return "boardAdd";
    }
}
