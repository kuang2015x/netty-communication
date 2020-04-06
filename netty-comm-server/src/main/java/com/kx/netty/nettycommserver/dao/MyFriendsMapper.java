package com.kx.netty.nettycommserver.dao;

import com.kx.netty.nettycommserver.pojo.MyFriends;
import com.kx.netty.nettycommserver.pojo.MyFriendsExample;
import java.util.List;

import com.kx.netty.nettycommserver.vo.MyFriendsVO;
import org.apache.ibatis.annotations.Param;

public interface MyFriendsMapper {
    int countByExample(MyFriendsExample example);

    int deleteByExample(MyFriendsExample example);

    int deleteByPrimaryKey(String id);

    int insert(MyFriends record);

    int insertSelective(MyFriends record);

    List<MyFriends> selectByExample(MyFriendsExample example);

    MyFriends selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") MyFriends record, @Param("example") MyFriendsExample example);

    int updateByExample(@Param("record") MyFriends record, @Param("example") MyFriendsExample example);

    int updateByPrimaryKeySelective(MyFriends record);

    int updateByPrimaryKey(MyFriends record);

    List<MyFriendsVO> queryMyFriends(String userId);
}