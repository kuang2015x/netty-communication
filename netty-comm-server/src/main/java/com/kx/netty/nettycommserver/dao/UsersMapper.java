package com.kx.netty.nettycommserver.dao;

import com.kx.netty.nettycommserver.pojo.Users;
import com.kx.netty.nettycommserver.pojo.UsersExample;
import java.util.List;

import com.kx.netty.nettycommserver.vo.FriendRequestVO;
import org.apache.ibatis.annotations.Param;

public interface UsersMapper {
    int countByExample(UsersExample example);

    int deleteByExample(UsersExample example);

    int deleteByPrimaryKey(String id);

    int insert(Users record);

    int insertSelective(Users record);

    List<Users> selectByExample(UsersExample example);

    Users selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByExample(@Param("record") Users record, @Param("example") UsersExample example);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);



    void batchUpdateMsgSigned(List<String> msgIdList);

}