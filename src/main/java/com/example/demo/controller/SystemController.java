package com.example.demo.controller;

import com.example.demo.dao.ChannelInfoDao;
import com.example.demo.dao.InterestInfoDao;
import com.example.demo.entity.InterestInfo;
import com.example.demo.result.ResultVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Init")
public class SystemController {

    private Logger logger = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private ChannelInfoDao channelInfoDao;
    @Autowired
    private InterestInfoDao interestInfoDao;

    @RequestMapping(value = "/ChannelClass",method = {RequestMethod.GET})
    public ResultVO setChannelClass(){
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg("S");
        resultVO.setData(channelInfoDao.findAll());
        return resultVO;
    }

    @RequestMapping(value = "/Channel",method = {RequestMethod.GET})
    public ResultVO setChannel(){
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg("S");
        List lists = new ArrayList();
        List<InterestInfo> interestInfos = interestInfoDao.findAll();
        lists.add(interestInfos);
        List<InterestInfo> hots = new ArrayList<>();
        for (int i = 0; i < interestInfos.size(); i++) {
            hots.add(interestInfos.get(i));
        }
        for (int i = 0; i <= interestInfos.size()-12; i++) {
            int num = (int) (Math.random()*hots.size()-1);
            hots.remove(num);
        }
        lists.add(hots);
        resultVO.setData(lists);
        return resultVO;
    }

}
