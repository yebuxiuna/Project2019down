package com.example.demo.result;

import lombok.Data;

@Data
public class ResultVOUtil {

    public static ResultVO error(Object data){
        ResultVO resultVO = new ResultVO();
        resultVO.setMsg("F");
        resultVO.setData(data);
        return resultVO;
    }

}
