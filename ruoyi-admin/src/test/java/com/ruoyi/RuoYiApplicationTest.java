package com.ruoyi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.ChatUser;
import com.ruoyi.system.service.ChatUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi
 * @Date：2021/11/18 16:44
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@SpringBootTest
public class RuoYiApplicationTest {
    @Test
    void contextLoads() {

    }

    @Autowired
    private ChatUserService chatUserService;
    @Test
    void testChatUserInsert(){
        ChatUser chatUser = new ChatUser();
        chatUser.setChatId((long)4545);

        chatUser.setUserName("123");
        chatUser.setUserSdp("sdp");
        chatUser.setUserCandidate("candidate");
        chatUser.setConnectTime(LocalDateTime.now());
        chatUserService.save(chatUser);

        chatUserService.save(chatUser);
    }
    @Test
    void testChatUserQuery(){
        QueryWrapper<ChatUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ChatUser.COL_USER_ID,147);
        List<ChatUser> list = chatUserService.list(queryWrapper);
        list.forEach(System.out::println);
    }
    @Test
    void testChatUserUpdate(){
        QueryWrapper<ChatUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ChatUser.COL_USER_ID,147);
        ChatUser chatUser = new ChatUser();
        chatUser.setUserName("456");
        chatUserService.update(chatUser,queryWrapper);
    }
    @Test
    void testChatUserDelete(){
        QueryWrapper<ChatUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ChatUser.COL_USER_ID,123);
        chatUserService.remove(queryWrapper);
    }

}
