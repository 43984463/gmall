package com.sherlock.gmall.service;

import com.sherlock.gmall.dao.BookDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * @auther Sherlock
 * @date 2020/10/22 23:31
 * @Description:
 */
@Service
public class BookService {

    //@Qualifier("bookDao")
    @Autowired(required = true)
    //@Resource(name = "bookDao2")
    //@Inject
    private BookDao bookDao;

    public void print(){
        System.out.println(bookDao);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("BookService{");
        sb.append("bookDao=").append(bookDao);
        sb.append('}');
        return sb.toString();
    }
}
