package com.example.demo.controller;

import com.example.demo.dao.ChannelInfoDao;
import com.example.demo.dao.InterestInfoDao;
import com.example.demo.entry.ChannelInfo;
import com.example.demo.entry.InterestInfo;
import com.example.demo.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/Init")
public class SystemController {
    @Autowired
    private ChannelInfoDao channelInfoDao;
    @Autowired
    private InterestInfoDao interestInfoDao;

    @RequestMapping(value = "/Channel",method = {RequestMethod.GET})
    public ResultVO setChannel(){
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg("S");
        List<List> lists = new ArrayList<>();
        List<ChannelInfo> channelInfos = channelInfoDao.findAll();
        List<InterestInfo> interestInfos = interestInfoDao.findAll();
        lists.add(channelInfos);
        lists.add(interestInfos);
        resultVO.setData(lists);
        return resultVO;
    }

}
