package com.sherlock.gmall.controller;

import com.sherlock.gmall.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @auther Sherlock
 * @date 2020/10/22 23:32
 * @Description:
 */
@Controller
public class BookController {

    @Autowired
    private BookService bookService;

}
