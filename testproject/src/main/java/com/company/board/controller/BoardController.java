package com.company.board.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.company.board.model.vo.Board;
import com.company.board.service.BoardService;
import com.company.member.model.vo.Member;
import com.company.reply.model.vo.Reply;
import com.company.reply.service.ReplyService;
import com.google.api.client.util.Value;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;



@Controller
public class BoardController {
	
	private static Storage storage = StorageOptions.getDefaultInstance().getService();
	
	@Value("${file.storage}")
	private Resource localFilePath;

	
	@Autowired 
	private BoardService boardService;
	
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	
	@RequestMapping(value="board", method = RequestMethod.GET)
	public ModelAndView selectBoardList(ModelAndView mv,String clickedPage, 
			@RequestParam(value = "p", defaultValue = "1")String pageNum,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException{
		
		
		logger.info("게시물 페이지에 온걸 환영하는 logger");
		//System.out.println("컨트롤러 최초접근");
		final int PAGE_SIZE = 5;
		final int PAGE_BLOCK = 3;
		int currentPage = 1;
		int listCount = 0;
		int pageCount = 0;
		try {
			listCount = boardService.getListCount();
			
			
		} catch (Exception e) {
			mv.addObject("msg", "게시판 오류발생");
			mv.addObject("url", "index");
			e.printStackTrace();
		}
		
		if (pageNum != null) {
			clickedPage = pageNum;
		}
		
		int startPage = ((int)((double)currentPage / PAGE_SIZE + 0.9) - 1) * PAGE_SIZE  + 1;
		
		pageCount = (listCount / PAGE_SIZE) + (listCount % PAGE_SIZE == 0 ? 0 : 1);
		
		int endPage = startPage + PAGE_SIZE - 1;
		if(endPage > pageCount) endPage=pageCount;
		int maxPage = (int)((double)listCount / PAGE_SIZE + 0.9);
		if (clickedPage != null) {
			if (Integer.parseInt(clickedPage) <= 0) {
				currentPage = 1;
			} else if(Integer.parseInt(clickedPage) > maxPage){
				currentPage = maxPage;
			}else {
				currentPage = Integer.parseInt(clickedPage);
			}
		}
		
		if (currentPage % PAGE_BLOCK == 0) {
			startPage = (currentPage / PAGE_BLOCK - 1) * PAGE_BLOCK + 1;
		}else {
			startPage = (currentPage / PAGE_BLOCK) * PAGE_BLOCK + 1;
		}
		
		
		List<Board> blist = null;
		
		try {
			blist = boardService.selectBoardList(currentPage, PAGE_SIZE);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mv.addObject("blist",blist);
		mv.addObject("currentPage",currentPage);
		mv.addObject("pageCount",pageCount);
		mv.addObject("startPage",startPage);
		mv.addObject("endPage",endPage);
		mv.addObject("maxPage",maxPage);
		
		//System.out.println("컨트롤러 마지막접근");
		mv.setViewName("/board/board_list");
		
		String gson = new Gson().toJson(blist);
		
//		System.out.println("컨트롤러단에서 gson반환값!!!!!!!!!!!!!!!!!!!" + gson);
//		mv.addObject("GSON", gson);
//		PrintWriter out = response.getWriter();
////		out.print(gson);
//		out.write(gson);
//		out.flush();
//		out.close();
		
		return mv;
	}
	
	@RequestMapping(value= "board", method = RequestMethod.POST)
	public void jqGridTest(ModelAndView mv,String clickedPage, 
			@RequestParam(value = "p", defaultValue = "1")String pageNum,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		
			final int PAGE_SIZE = 5;
			final int PAGE_BLOCK = 3;
			int currentPage = 1;
			int listCount = 0;
			int pageCount = 0;
			try {
				listCount = boardService.getListCount();
				
				
			} catch (Exception e) {
				mv.addObject("msg", "게시판 오류발생");
				mv.addObject("url", "index");
				e.printStackTrace();
			}
			
			if (pageNum != null) {
				clickedPage = pageNum;
			}
			
			int startPage = ((int)((double)currentPage / PAGE_SIZE + 0.9) - 1) * PAGE_SIZE  + 1;
			
			pageCount = (listCount / PAGE_SIZE) + (listCount % PAGE_SIZE == 0 ? 0 : 1);
			
			int endPage = startPage + PAGE_SIZE - 1;
			if(endPage > pageCount) endPage=pageCount;
			int maxPage = (int)((double)listCount / PAGE_SIZE + 0.9);
			if (clickedPage != null) {
				if (Integer.parseInt(clickedPage) <= 0) {
					currentPage = 1;
				} else if(Integer.parseInt(clickedPage) > maxPage){
					currentPage = maxPage;
				}else {
					currentPage = Integer.parseInt(clickedPage);
				}
			}
			
			if (currentPage % PAGE_BLOCK == 0) {
				startPage = (currentPage / PAGE_BLOCK - 1) * PAGE_BLOCK + 1;
			}else {
				startPage = (currentPage / PAGE_BLOCK) * PAGE_BLOCK + 1;
			}
			
			
			List<Board> blist = null;
					
			try {
				blist = boardService.selectBoardListAll();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mv.addObject("blist",blist);
			mv.addObject("currentPage",currentPage);
			mv.addObject("pageCount",pageCount);
			mv.addObject("startPage",startPage);
			mv.addObject("endPage",endPage);
			mv.addObject("maxPage",maxPage);
//			if (member != null) {
//				mv.addObject("member",memberId.getHouseNum());
//			}
			//System.out.println("컨트롤러 마지막접근");
			mv.setViewName("/board/board_list");
			
			String gson = new Gson().toJson(blist);
			
//			System.out.println("컨트롤러단에서 gson반환값!!!!!!!!!!!!!!!!!!!" + gson);
			mv.addObject("GSON", gson);
			PrintWriter out = response.getWriter();
//			out.print(gson);
			out.write(gson);
			out.flush();
			out.close();
			
			
	}
	
	
	@RequestMapping(value = "board-view", method = RequestMethod.GET)	
	public ModelAndView selectBoardView(ModelAndView mv, @RequestParam(value="no", defaultValue="0")int brno,
			HttpServletRequest request) {
		String viewPage = "";
		
		Member member= (Member)request.getSession().getAttribute("member");
		
		
		List<Board> boardList = null;
		try {
			boardList = boardService.selectBoardView(brno);
			viewPage = "/board/board_contentview";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Reply> replylist = null;
		Reply rvo = new Reply(brno);
//		System.out.println("brno의 값" + brno);
//		System.out.println("컨트롤러 rvo의 값" + rvo);
		boardService.boardPostviewUpdate(brno);
		
		try {
			replylist= boardService.selectReplyList(rvo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		mv.addObject("blist",boardList);
		if(member != null) {
			mv.addObject("member", member);
			
		}
		mv.setViewName(viewPage);
		
		if(replylist != null) {
			mv.addObject("replylist", replylist);
		}
		return mv;
	}
	
	@RequestMapping(value="board-insert", method=RequestMethod.GET)
	public String insertBoard() {
		
		return "./board/board_insert";
	}
	
	//@PostMapping("board-insert")
	@RequestMapping(value="board-insert", method= {RequestMethod.POST})
	public ModelAndView insertBoard(ModelAndView mv,@RequestParam(value="t")String title,
			@RequestParam(value="c" )String Content,
			@RequestParam(value="image", required=false) MultipartFile image,
			@RequestParam(value="file", required=false) MultipartFile file,
			HttpServletRequest request
			) {
		
		
		int boardResult = 0;
		
		String imgsrc = "";
		String filesrc = "";
		//이미지를 서버에 저장 ftp로 바꿔야함
		//imgsrc = googleCloudPlatformUpload(image);
			if(imgsrc != null){
				logger.info("이미지 저장주소: " + imgsrc);
			}
		//파일을 서버에 저장 ftp로 바꿔야함
		//filesrc = googleCloudPlatformUpload(file);
			if(filesrc != null) {
				logger.info("파일 저장주소: " + filesrc);
			}
		
		Member member =  (Member)request.getSession().getAttribute("member");
		//int member = (((Member)request.getSession().getAttribute("member"));
		int userNo =  member.getMm_userNo() ;
		System.out.println("userNo는:" + member.getMm_userNo());
		
		
		
		if( (image == null) && (file == null) ) {
			//이미지없을 경우
			Board bvo = new Board(userNo,title,Content);
			boardResult = boardService.insertBoard(bvo);
		}else {
			//이미지나 파일있을 경우
			Board bvo = new Board(userNo,title,Content,imgsrc,filesrc);
			boardResult = boardService.insertBoardwithImg(bvo);
		}
		
		if(boardResult == 0) {
			logger.error("입력 실패");
		}else {
			logger.info("입력 성공");
		}
		
		
		mv.addObject("result", boardResult);
		mv.setViewName("redirect: ./board");
		
		return mv;
		
	}
	@RequestMapping(value = "board-update",method = RequestMethod.GET)
	public ModelAndView updateBoard(ModelAndView mv,
			@RequestParam(value="no", defaultValue ="0")int brno) {
		String viewPage = "";
		
		List<Board> boardList = null;
		try {
			boardList = boardService.selectBoardView(brno);
			viewPage = "/board/board_update";
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mv.addObject("blist",boardList);
		mv.setViewName(viewPage);
		
		return mv;
	}
	
	@RequestMapping(value = "board-update", method = RequestMethod.POST)
	public ModelAndView updateBoard(ModelAndView mv, @RequestParam(value="t")String title,
			@RequestParam(value="c") String content,
			@RequestParam(value="no",defaultValue="0") int brno,
			@RequestParam(value="image",required=false)MultipartFile image,
			@RequestParam(value="files",required=false)MultipartFile file,
			HttpServletRequest request) {
		String viewPage = "";
		int boardResult = 0;
		
		
		String imgsrc = "";
		String filesrc = "";
		//이미지를 서버에 저장 ftp로 바꿔야함
		//imgsrc = googleCloudPlatformUpload(image);
			if(imgsrc != null){
					logger.info("이미지 저장주소: " + imgsrc);
			}
		//파일을 서버에 저장 ftp로 바꿔야함
		//filesrc = googleCloudPlatformUpload(file);
			if(filesrc != null) {
				logger.info("파일 저장주소: " + filesrc);
			}
			
			
		if( (image == null) && (file == null) )
		{
			Board bvo = new Board( title, content , brno);
			boardResult = boardService.updateBoard(bvo);
		}else {
			Board bvo = new Board(title, content,imgsrc,filesrc , brno);
			boardResult = boardService.updateBoard(bvo);
		}
		
		if(boardResult == 0) {
			logger.error("업데이트 실패");
		}else {
			logger.info("업데이트 성공");
			
		}
		viewPage= "redirect: ./board";	
		
		mv.addObject("result", boardResult);
		mv.setViewName(viewPage);
		return mv;
	}
	
	@RequestMapping(value="board-delete", method = RequestMethod.GET )
	public String deleteBoard(ModelAndView mv, @RequestParam(value="no", defaultValue= "0") int brno) {
		
		Board bvo = new Board(brno);
		boardService.deleteBoard(brno);
		
		
		return "redirect: ./board";
	}
	
	
	
	
	@RequestMapping(value="jqgrid_list", method = RequestMethod.GET)
	public ModelAndView jqgridBoardList(ModelAndView mv) throws Exception{
		
		mv.setViewName("/jqgrid_list");
		
		return mv;
		
		
	}
		
		
		
	
	

}
