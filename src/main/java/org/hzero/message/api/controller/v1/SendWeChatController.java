package org.hzero.message.api.controller.v1;

import org.hzero.boot.message.config.MessageClientProperties;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.WeChatSender;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.message.app.service.WeChatSendService;
import org.hzero.message.config.MessageSwaggerApiConfig;
import org.hzero.message.domain.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 微信消息发送
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/16 14:15
 */
@Api(tags = MessageSwaggerApiConfig.WE_CHAT_MESSAGE)
@RestController("sendWeChatController.v1")
@RequestMapping("/v1/{organizationId}/messages/wechat")
public class SendWeChatController extends BaseController {

    private final WeChatSendService weChatSendService;
    private final MessageClientProperties messageClientProperties;

    @Autowired
    public SendWeChatController(WeChatSendService weChatSendService,
                                MessageClientProperties messageClientProperties) {
        this.weChatSendService = weChatSendService;
        this.messageClientProperties = messageClientProperties;
    }

    @ApiOperation(value = "发送一条公众号消息，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/official")
    public ResponseEntity<Message> sendMessageWithTemplate(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                           @RequestBody WeChatSender weChatSender) {
        weChatSender.setTenantId(organizationId).setLang(StringUtils.hasText(weChatSender.getLang()) ? weChatSender.getLang() : messageClientProperties.getDefaultLang());
        Assert.notNull(weChatSender.getMessageCode(), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(weChatSender, MessageSender.WebMessage.class);
        return Results.success(weChatSendService.sendOfficialMessage(weChatSender));
    }

    @ApiOperation(value = "发送企业微信应用，指定模板和参数")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping("/enterprise")
    public ResponseEntity<Message> sendWeChatEnterprise(@PathVariable @ApiParam(value = "租户ID", required = true) long organizationId,
                                                        @RequestBody WeChatSender weChatSender) {
        weChatSender.setTenantId(organizationId).setLang(StringUtils.hasText(weChatSender.getLang()) ? weChatSender.getLang() : messageClientProperties.getDefaultLang());
        Assert.notNull(weChatSender.getMessageCode(), BaseConstants.ErrorCode.DATA_INVALID);
        Assert.notNull(weChatSender.getAgentId(), BaseConstants.ErrorCode.DATA_INVALID);
        validObject(weChatSender, MessageSender.WebMessage.class);
        return Results.success(weChatSendService.sendEnterpriseMessage(weChatSender));
    }
}
