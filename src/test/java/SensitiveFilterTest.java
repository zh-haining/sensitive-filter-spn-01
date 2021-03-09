import com.google.common.collect.Lists;
import com.spnc.entity.RuleBean;
import com.spnc.ikexpression.FilterEngine;
import com.spnc.tools.ComplexDFAUtil;
import org.wltea.expression.PreparedExpression;

import java.util.List;

/**
 * @desc: 测试
 * @author: zh_haining
 * @date: 2021/3/9 下午1:28
 */
public class SensitiveFilterTest {

    public static void main(String[] args) throws Exception {
        FilterEngine filterEngine = new FilterEngine();
        filterEngine.init();
        List<String>contents = buildContents();
        for (String content : contents) {
            long startTime = System.currentTimeMillis();
            boolean result = false;
            //规则过滤
            List<RuleBean> type1Rules = filterEngine.getType1Rules();
            if (null != type1Rules && type1Rules.size() > 0) {
                Object positionMap = ComplexDFAUtil.filterTo(content, ComplexDFAUtil.MinMatchTYpe);
                String logMsg = String.format("待检测内容: %s , 位置信息positionMap: %s", content, positionMap.toString());
                System.out.println(logMsg);
                for (RuleBean type1Rule : type1Rules) {
                    PreparedExpression pe = type1Rule.getPreparedExpression();
                    pe.setArgument("positionMap", positionMap);
                    //执行表达式
                    boolean flag = (boolean) pe.execute();
                    if (flag) {

                        logMsg = String.format("已经命中 %s 规则 | content: %s", type1Rule.getRuleCode(), content);
                        System.out.println(logMsg);
                        result = true;
                        break;
                    }
                }
            }
            if (!result) {
                //如果没有命中的
                String logMsg = String.format("全部规则未命中 | 待检测内容: %s", content);
                System.out.println(logMsg);
            }
            String logMsg = String.format("敏感词检测完毕 | 共耗时：%s ms", System.currentTimeMillis() - startTime);
            System.out.println(logMsg);
        }
    }

    public static List<String>buildContents(){
        List<String>contents = Lists.newArrayList();
        contents.add("集团公关部发布了政策");//false;
        contents.add("集团公关部发布了手册");//true;
        contents.add("公关人员花了钱，贿赂了材料供应商");//true;
        contents.add("公关人员花了很多很多钱，贿赂了材料供应商");//false;
        return contents;
    }

}
