package com.kx.netty.nettycommserver.service.serviceImpl;

import com.kx.netty.nettycommserver.common.MsgActionEnum;
import com.kx.netty.nettycommserver.common.MsgSignFlagEnum;
import com.kx.netty.nettycommserver.common.SearchFriendsStatusEnum;
import com.kx.netty.nettycommserver.dao.ChatMsgMapper;
import com.kx.netty.nettycommserver.dao.FriendsRequestMapper;
import com.kx.netty.nettycommserver.dao.MyFriendsMapper;
import com.kx.netty.nettycommserver.dao.UsersMapper;
import com.kx.netty.nettycommserver.netty.DataContent;
import com.kx.netty.nettycommserver.netty.UserChannelRel;
import com.kx.netty.nettycommserver.pojo.*;
import com.kx.netty.nettycommserver.service.UserService;
import com.kx.netty.nettycommserver.utils.FastDFSClient;
import com.kx.netty.nettycommserver.utils.FileUtils;
import com.kx.netty.nettycommserver.utils.JsonUtils;
import com.kx.netty.nettycommserver.utils.QRCodeUtils;
import com.kx.netty.nettycommserver.vo.FriendRequestVO;
import com.kx.netty.nettycommserver.vo.MyFriendsVO;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.collections.CollectionUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.kx.netty.nettycommserver.netty.ChatMsg;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 　　* @description: TODO
 * 　　* @author kx
 * 　　* @date 2020/03/21 21:51
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;

    @Override
    public boolean queryUsernameIsExist(String username) {

        UsersExample usersExample = new UsersExample();
        UsersExample.Criteria criterion = usersExample.createCriteria();
        criterion.andUsernameEqualTo(username);
        List<Users> list = usersMapper.selectByExample(usersExample);
        return CollectionUtils.isEmpty(list) ? false : true;
    }

    @Override
    public Users queryUserForLogin(String username, String pwd) {
        UsersExample userExample = new UsersExample();
        UsersExample.Criteria criteria = userExample.createCriteria();

        criteria.andUsernameEqualTo(username);
        criteria.andPasswordEqualTo(pwd);

        List<Users> list = usersMapper.selectByExample(userExample);

        return list.get(0);
    }

    @Override
    public Users saveUser(Users user) {

        String userId = sid.nextShort();

        // 为每个用户生成一个唯一的二维码
        String qrCodePath = "F://user" + userId + "qrcode.png";
        // muxin_qrcode:[username]
        qrCodeUtils.createQRCode(qrCodePath, "muxin_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);

        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setQrcode(qrCodeUrl);

        user.setId(userId);
        usersMapper.insertSelective(user);

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {

        usersMapper.updateByPrimaryKey(user);

        return usersMapper.selectByPrimaryKey(user.getId());

    }

    @Override
    public Integer preconditionSearchFriends(String myUserId, String friendUsername) {

        Users users = queryUserInfoByUsername(friendUsername);

        if (users == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }

        if (users.getId().equals(myUserId)) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }

        MyFriendsExample mfe = new MyFriendsExample();
        MyFriendsExample.Criteria mfc = mfe.createCriteria();
        mfc.andMyUserIdEqualTo(myUserId);
        mfc.andMyFriendUserIdEqualTo(users.getId());
        List<MyFriends> myFriendsRel = myFriendsMapper.selectByExample(mfe);
        if (myFriendsRel != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    @Override
    public Users queryUserInfoByUsername(String username) {

        UsersExample usersExample = new UsersExample();
        UsersExample.Criteria criterion = usersExample.createCriteria();
        criterion.andUsernameEqualTo(username);
        List<Users> list = usersMapper.selectByExample(usersExample);


        return list.get(0);
    }

    @Override
    public void sendFriendRequest(String myUserId, String friendUsername) {

        Users users = queryUserInfoByUsername(friendUsername);

        FriendsRequestExample friendsRequestExample = new FriendsRequestExample();
        FriendsRequestExample.Criteria criterion = friendsRequestExample.createCriteria();
        criterion.andAcceptUserIdEqualTo(myUserId);
        criterion.andAcceptUserIdEqualTo(users.getId());
        List<FriendsRequest> list = friendsRequestMapper.selectByExample(friendsRequestExample);

        if (CollectionUtils.isEmpty(list)){
            String requestId = sid.nextShort();

            FriendsRequest request = new FriendsRequest();
            request.setId(requestId);
            request.setSendUserId(myUserId);
            request.setAcceptUserId(users.getId());
            request.setRequestDateTime(new Date());
            friendsRequestMapper.insert(request);
        }

    }

    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {

        return usersMapper.queryFriendRequestList(acceptUserId);
    }

    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {

        FriendsRequestExample friendRequest = new FriendsRequestExample();
        FriendsRequestExample.Criteria criterion = friendRequest.createCriteria();
        criterion.andAcceptUserIdEqualTo(acceptUserId);
        criterion.andSendUserIdEqualTo(sendUserId);
        friendsRequestMapper.deleteByExample(friendRequest);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {

        saveFriends(sendUserId, acceptUserId);
        saveFriends(acceptUserId, sendUserId);
        deleteFriendRequest(sendUserId, acceptUserId);

        Channel sendChannel = UserChannelRel.get(sendUserId);
        if (sendChannel != null) {
            // 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);

            sendChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            JsonUtils.objectToJson(dataContent)));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        String recordId = sid.nextShort();
        myFriends.setId(recordId);
        myFriends.setMyFriendUserId(acceptUserId);
        myFriends.setMyUserId(sendUserId);
        myFriendsMapper.insert(myFriends);
    }

    @Override
    public List<MyFriendsVO> queryMyFriends(String userId) {
        List<MyFriendsVO> myFirends = myFriendsMapper.queryMyFriends(userId);
        return myFirends;
    }

    @Override
    public String saveMsg(ChatMsg chatMsg) {

        com.kx.netty.nettycommserver.pojo.ChatMsg msgDB = new com.kx.netty.nettycommserver.pojo.ChatMsg();
        String msgId = sid.nextShort();
        msgDB.setId(msgId);
        msgDB.setAcceptUserId(chatMsg.getReceiverId());
        msgDB.setSendUserId(chatMsg.getSenderId());
        msgDB.setCreateTime(new Date());
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
        msgDB.setMsg(chatMsg.getMsg());

        chatMsgMapper.insert(msgDB);

        return msgId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {

        usersMapper.batchUpdateMsgSigned(msgIdList);
    }

    @Override
    public List<com.kx.netty.nettycommserver.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {

        ChatMsgExample chatMsgExample = new ChatMsgExample();
        ChatMsgExample.Criteria criterion = chatMsgExample.createCriteria();
        criterion.andSignFlagEqualTo(0);
        criterion.andAcceptUserIdEqualTo(acceptUserId);
        List<com.kx.netty.nettycommserver.pojo.ChatMsg> result = chatMsgMapper.selectByExample(chatMsgExample);

        return result;
    }
}
