package com.inherit;

import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.loyer.common.core.constant.SpecialCharsConst;
import com.loyer.common.dedicine.utils.StringUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * docker-java执行命令的回调
 *
 * @author kuangq
 * @date 2022-08-22 16:15
 */
public class ExecStartCmdCallback extends ResultCallbackTemplate<ExecStartCmdCallback, Frame> {

    private final List<String> stdoutList;

    private final List<String> stderrList;

    public ExecStartCmdCallback(List<String> stdoutList, List<String> stderrList) {
        this.stdoutList = stdoutList;
        this.stderrList = stderrList;
    }

    @Override
    public void onNext(Frame frame) {
        if (frame == null) {
            return;
        }
        List<String> outList = Arrays.stream(StringUtil.encode(frame.getPayload()).split(SpecialCharsConst.LINE_FEED)).collect(Collectors.toList());
        if (frame.getStreamType() == StreamType.STDERR) {
            if (this.stderrList != null) {
                this.stderrList.addAll(outList);
            }
        } else {
            if (this.stdoutList != null) {
                this.stdoutList.addAll(outList);
            }
        }
    }
}