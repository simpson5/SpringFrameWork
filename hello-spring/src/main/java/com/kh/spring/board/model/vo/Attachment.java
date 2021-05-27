package com.kh.spring.board.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
	private int no;
    private int boardNo;
    private String originalFilename;
    private String renamedFilename;
    private Date uploadDate;
    private int downloadCount;
    private boolean status; // status ----'Y', 'N'
}
