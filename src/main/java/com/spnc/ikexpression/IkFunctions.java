package com.spnc.ikexpression;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @desc: IKExpression 自定义函数
 * @author: zh_haining
 * @date: 2021/2/4 下午2:36
 */
public class IkFunctions {
    public final static String STRAR = "start";
    public final static String END = "end";

    public Boolean contains(Object positionMap, String word) {
        Map<String, List<Integer>> positionMapx = (Map<String, List<Integer>>) positionMap;
        return positionMapx.containsKey(word);
    }

    public Boolean within(Object positionMap, String a, String b, int step) {
        Map<String, List<Integer>> positionMapx = (Map<String, List<Integer>>) positionMap;
        List<Map<String, Integer>> m = Lists.newArrayList();
        List<Map<String, Integer>> n = Lists.newArrayList();

        Arrays.stream(a.split("###")).filter(positionMapx::containsKey).forEach(p -> {
            positionMapx.get(p).forEach(len -> {
                Map<String, Integer> map = Maps.newHashMap();
                map.put(IkFunctions.STRAR, len);
                map.put(IkFunctions.END, len + p.length() - 1);
                m.add(map);
            });
        });

        Arrays.stream(b.split("###")).filter(positionMapx::containsKey).forEach(p -> {
            positionMapx.get(p).forEach(len -> {
                Map<String, Integer> map = Maps.newHashMap();
                map.put(IkFunctions.STRAR, len);
                map.put(IkFunctions.END, len + p.length() - 1);
                n.add(map);
            });
        });
        return this.jude(m, n, step);
    }

    public Boolean withinUp(Object positionMap, String a, String b, int step) {
        Map<String, List<Integer>> positionMapx = (Map<String, List<Integer>>) positionMap;
        List<Map<String, Integer>> m = Lists.newArrayList();
        List<Map<String, Integer>> n = Lists.newArrayList();

        Arrays.stream(a.split("###")).filter(positionMapx::containsKey).forEach(p -> {
            positionMapx.get(p).forEach(len -> {
                Map<String, Integer> map = Maps.newHashMap();
                map.put(IkFunctions.STRAR, len);
                map.put(IkFunctions.END, len + p.length() - 1);
                m.add(map);
            });
        });

        Arrays.stream(b.split("###")).filter(positionMapx::containsKey).forEach(p -> {
            positionMapx.get(p).forEach(len -> {
                Map<String, Integer> map = Maps.newHashMap();
                map.put(IkFunctions.STRAR, len);
                map.put(IkFunctions.END, len + p.length() - 1);
                n.add(map);
            });
        });
        return this.jude(m, n, step);
    }

    /**
     * 判断两个数组元素之间的差值有没有小于等于指定值的
     *
     * @date 2021/2/4 下午7:23
     * @auther zh_haining
     */
    private boolean jude(List<Map<String, Integer>> a, List<Map<String, Integer>> b, int d) {
        for (Map<String, Integer> aMap : a) {
            int astart = aMap.get("start");
            int aend = aMap.get("end");
            for (Map<String, Integer> bMap : b) {
                int bstart = bMap.get("start");
                int bend = bMap.get("end");
                if (astart <= bstart) {
                    if ((bstart - aend) - 1 < d) {
                        return true;
                    }
                } else {
                    if ((astart - bend) - 1 < d) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
