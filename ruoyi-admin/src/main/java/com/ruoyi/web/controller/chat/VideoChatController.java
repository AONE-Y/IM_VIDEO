package com.ruoyi.web.controller.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.ChatUser;
import com.ruoyi.system.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Project：RuoYi-Vue
 * @Package：com.ruoyi.web.controller.chat
 * @Date：2021/11/19 16:52
 * @Author：ANONE
 * @Address： HaiKou·China
 * @Description:
 * @Modified By: ANONE
 */
@RestController
@RequestMapping("/chat/videoChat")
public class VideoChatController {

    @Autowired
    private ChatUserService chatUserService;

    @PostMapping("add")
    public AjaxResult addChatVideoInfo(@RequestBody ChatUser chatUser){
        if (chatUserService.save(chatUser)) {
            return AjaxResult.success("客户端信息添加成功.....");
        }
        return AjaxResult.error("客户端信息添加失败.....");
    }

    @GetMapping("delete")
    public AjaxResult deleteChatVideoInfo(String userName){
        QueryWrapper<ChatUser> chatUserQueryWrapper = new QueryWrapper<>();
        chatUserQueryWrapper.eq(ChatUser.COL_USER_NAME,userName);
        if (chatUserService.remove(chatUserQueryWrapper)) {
            return AjaxResult.success("操作成功.....");
        }
        return AjaxResult.error("操作失败.....");
    }
}
