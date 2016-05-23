package cn.wydewy.wycards.controller;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import cn.wydewy.wycards.model.Card;
import cn.wydewy.wycards.model.SetOfCards;
import cn.wydewy.wycards.util.CardType;
import cn.wydewy.wycards.view.CardsGameView;

/*
 * AI
 * */
public class CardsAI {

    public static int dizhuFlag; //地主id
    public static int currentFlag;//当前id
    public static int oppoerFlag;//对手牌id
    public static CardsGameView view;

    //得到电脑最佳选牌
    public static List<Card> getBestAI(List<Card> curList, List<Card> oppo) {
        List<Card> list = new Vector<Card>(curList);
        SetOfCards SetOfCards = new SetOfCards();
        SetOfCards SetOfCardsSingle = new SetOfCards();

        //找出所有对子,3带，炸弹，飞机，双顺，单顺
        CardsAI.getTwo(list, SetOfCards);
        CardsAI.getThree(list, SetOfCards);
        CardsAI.get123(list, SetOfCards);
        CardsAI.getBoomb(list, SetOfCards);
        CardsAI.getTwoTwo(list, SetOfCards);
        CardsAI.getPlane(list, SetOfCards);
        CardsAI.getSingle(list, SetOfCards);
        //去除SetOfCards里面独立牌型
        CardsAI.checkSetOfCards(list, SetOfCards, SetOfCardsSingle);
        //现在分别计算每种可能性的权值,和手数，取最大的那个(注意有些牌型是相关的，组成这个就不能组成其他)
        //所以组成一种牌型前要判断这种牌型的牌还是否存在
        //先比较手数再比较权值
        SetOfCards bestSetOfCards = null, mySetOfCards = null;
        int value = 0;
        int time = 99;
        for (int i = 0, len1 = SetOfCards.a4.size(); i <= len1; i++)
            for (int j = 0, len2 = SetOfCards.a3.size(); j <= len2; j++)
                for (int k = 0, len3 = SetOfCards.a2.size(); k <= len3; k++)
                    for (int l = 0, len4 = SetOfCards.a12345.size(); l <= len4; l++)
                        for (int m = 0, len5 = SetOfCards.a112233.size(); m <= len5; m++)
                            for (int n = 0, len6 = SetOfCards.a111222.size(); n <= len6; n++) {
                                List<Card> newlist = new Vector<Card>(list);
                                //我承认这个循环有点长..相信你的CPU
                                bestSetOfCards = CardsAI.getBestSetOfCards(newlist, SetOfCards, new int[]{i, j, k, l, m, n});
                                //加上独立的牌
                                bestSetOfCards.a1.addAll(SetOfCardsSingle.a1);
                                bestSetOfCards.a2.addAll(SetOfCardsSingle.a2);
                                bestSetOfCards.a3.addAll(SetOfCardsSingle.a3);
                                bestSetOfCards.a4.addAll(SetOfCardsSingle.a4);
                                bestSetOfCards.a12345.addAll(SetOfCardsSingle.a12345);
                                bestSetOfCards.a112233.addAll(SetOfCardsSingle.a112233);
                                bestSetOfCards.a111222.addAll(SetOfCardsSingle.a111222);
                                //加上单牌
                                for (Card singleCard : newlist) {
                                    bestSetOfCards.a1.add(singleCard.getName());
                                }
                                //计算手数，计算权值
                                if (CardsAI.getTimes(bestSetOfCards) < time) {
                                    time = CardsAI.getTimes(bestSetOfCards);
                                    mySetOfCards = bestSetOfCards;
                                } else if (CardsAI.getTimes(bestSetOfCards) == time && CardsAI.getCountValues(bestSetOfCards) > value) {
                                    value = CardsAI.getCountValues(bestSetOfCards);
                                    mySetOfCards = bestSetOfCards;
                                }

                            }
        //开始出牌
        List<Card> showCardslList = new Vector<Card>();
        if (oppo == null) {
            Log.i("mylog", "AI oppo==null");
            //主动出牌
            showCards(mySetOfCards, showCardslList, curList);
        } else {
            showCards2(mySetOfCards, showCardslList, curList, oppo);
        }

        //被动出牌
        if (showCardslList == null || showCardslList.size() == 0)
            return null;
        return showCardslList;
    }

    //被动出牌
    public static void showCards2(SetOfCards SetOfCards, List<Card> to, List<Card> from, List<Card> oppo) {
        //oppo是对手出的牌,from是自己已有的牌,to是药走出的牌
        List<String> list = new Vector<String>();//装要走出的牌的name
        CardType cType = CardsAI.jugdeType(oppo);
        //按重复数排序,这样只需比较第一张牌
        oppo = CardsAI.getOrder2(oppo);
        switch (cType) {
            case c1:
                //如果队友出牌比较大，我就不接，下面就不注释了
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 13))
                    break;
                for (int len = SetOfCards.a1.size(), i = len - 1; i >= 0; i--) {
                    if ((!isLessFive()) && CardsAI.getValueByName(SetOfCards.a1.get(i)) >= 16)
                        break;
                    if (CardsAI.getValueByName(SetOfCards.a1.get(i)) > CardsAI.getValue(oppo.get(0))) {
                        list.add(SetOfCards.a1.get(i));
                        break;
                    }
                }
                if (CardsAI.isLessFive()) {
                    for (int i = 0, leni = from.size(); i < leni; i++) {
                        if (CardsAI.getValue(from.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            to.add(from.get(i));
                            break;
                        }
                    }
                    return;
                }
                break;
            case c2:
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 12))
                    break;
                for (int len = SetOfCards.a2.size(), i = len - 1; i >= 0; i--) {
                    if ((!isLessFive()) && CardsAI.getValueByName(SetOfCards.a2.get(i)) >= 15)
                        break;
                    if (CardsAI.getValueByName(SetOfCards.a2.get(i)) > CardsAI.getValue(oppo.get(0))) {
                        list.add(SetOfCards.a2.get(i));
                        break;
                    }
                }
                if (CardsAI.isLessFive() && SetOfCards.a3.size() > 0) {
                    //从3带拆
                    for (int len = SetOfCards.a3.size(), i = len - 1; i >= 0; i--) {

                        if (CardsAI.getValueByName(SetOfCards.a3.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            List<Card> t = CardsAI.getCardsByName(from, SetOfCards.a3.get(i));
                            to.add(t.get(0));
                            to.add(t.get(1));
                            return;
                        }
                    }
                }
                break;
            case c3:
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 13))
                    break;
                for (int len = SetOfCards.a3.size(), i = len - 1; i >= 0; i--) {
                    if ((!isLessFive()) && CardsAI.getValueByName(SetOfCards.a3.get(i)) >= 15)
                        break;
                    if (CardsAI.getValueByName(SetOfCards.a3.get(i)) > CardsAI.getValue(oppo.get(0))) {
                        list.add(SetOfCards.a3.get(i));
                        break;
                    }
                }
                break;
            case c31:
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 9))
                    break;
                int len1 = SetOfCards.a3.size();
                int len2 = SetOfCards.a1.size();
                if (!(len1 < 1 || len2 < 1)) {
                    for (int len = len1, i = len - 1; i >= 0; i--) {
                        if ((!isLessFive()) && CardsAI.getValueByName(SetOfCards.a3.get(i)) >= 15)
                            break;
                        if (CardsAI.getValueByName(SetOfCards.a3.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            list.add(SetOfCards.a3.get(i));
                            break;
                        }
                    }
                    if (list.size() > 0)
                        list.add(SetOfCards.a1.get(len2 - 1));
                }
                break;
            case c32:
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 9))
                    break;
                len1 = SetOfCards.a3.size();
                len2 = SetOfCards.a2.size();
                if (!(len1 < 1 || len2 < 1)) {
                    for (int len = len1, i = len - 1; i >= 0; i--) {
                        if ((!isLessFive()) && CardsAI.getValueByName(SetOfCards.a3.get(i)) >= 15)
                            break;
                        if (CardsAI.getValueByName(SetOfCards.a3.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            list.add(SetOfCards.a3.get(i));
                            break;
                        }
                    }
                    if (list.size() > 0)
                        list.add(SetOfCards.a2.get(len2 - 1));
                }
                break;
            case c411:
                if (CardsAI.isFriend())
                    break;
                len1 = SetOfCards.a4.size();
                len2 = SetOfCards.a1.size();
                if (!(len1 < 1 || len2 < 2)) {
                    for (int len = len1, i = len - 1; i >= 0; i--) {
                        if (CardsAI.getValueByName(SetOfCards.a4.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            list.add(SetOfCards.a4.get(i));
                            break;
                        }
                    }
                    if (list.size() > 0) {
                        list.add(SetOfCards.a1.get(len2 - 1));
                        list.add(SetOfCards.a1.get(len2 - 2));
                    }
                }
                break;
            case c422:
                if (CardsAI.isFriend())
                    break;
                len1 = SetOfCards.a4.size();
                len2 = SetOfCards.a2.size();
                if (!(len1 < 1 || len2 < 2)) {
                    for (int len = len1, i = len - 1; i >= 0; i--) {
                        if (CardsAI.getValueByName(SetOfCards.a4.get(i)) > CardsAI.getValue(oppo.get(0))) {
                            list.add(SetOfCards.a4.get(i));
                            break;
                        }
                    }
                    if (list.size() > 0) {
                        list.add(SetOfCards.a2.get(len2 - 1));
                        list.add(SetOfCards.a2.get(len2 - 2));
                    }
                }
                break;
            case c123:
                if (CardsAI.isFriend() && (CardsAI.getValue(oppo.get(0)) >= 9))
                    break;
                for (int len = SetOfCards.a12345.size(), i = len - 1; i >= 0; i--) {
                    String[] s = SetOfCards.a12345.get(i).split(",");
                    if (s.length == oppo.size()
                            && CardsAI.getValueByName(SetOfCards.a12345.get(i)) > CardsAI.getValue(oppo
                            .get(0))) {
                        list.add(SetOfCards.a12345.get(i));
                        break;
                    }
                }
                break;
            case c1122:
                if (CardsAI.isFriend())
                    break;
                for (int len = SetOfCards.a112233.size(), i = len - 1; i >= 0; i--) {
                    String[] s = SetOfCards.a112233.get(i).split(",");
                    if (s.length == oppo.size()
                            && CardsAI.getValueByName(SetOfCards.a112233.get(i)) > CardsAI.getValue(oppo
                            .get(0))) {
                        list.add(SetOfCards.a112233.get(i));
                        break;
                    }
                }
                break;
            case c11122234:
                if (CardsAI.isFriend())
                    break;
                len1 = SetOfCards.a111222.size();
                len2 = SetOfCards.a1.size();

                if (!(len1 < 1 || len2 < 2)) {
                    for (int i = len1 - 1; i >= 0; i--) {
                        String[] s = SetOfCards.a111222.get(i).split(",");
                        if ((s.length / 3 <= len2)
                                && (s.length * 4 == oppo.size())
                                && CardsAI.getValueByName(SetOfCards.a111222.get(i)) > CardsAI.getValue(oppo
                                .get(0))) {
                            list.add(SetOfCards.a111222.get(i));
                            for (int j = 1; j <= s.length / 3; j++)
                                list.add(SetOfCards.a1.get(len2 - j));
                        }
                    }
                }

                break;
            case c1112223344:
                if (CardsAI.isFriend())
                    break;
                len1 = SetOfCards.a111222.size();
                len2 = SetOfCards.a2.size();

                if (!(len1 < 1 || len2 < 2)) {
                    for (int i = len1 - 1; i >= 0; i--) {
                        String[] s = SetOfCards.a111222.get(i).split(",");
                        if ((s.length / 3 <= len2)
                                && (s.length * 4 == oppo.size())
                                && CardsAI.getValueByName(SetOfCards.a111222.get(i)) > CardsAI.getValue(oppo
                                .get(0))) {
                            list.add(SetOfCards.a111222.get(i));
                            for (int j = 1; j <= s.length / 3; j++)
                                list.add(SetOfCards.a2.get(len2 - j));
                        }
                    }
                }
                break;
            case c4:
                for (int len = SetOfCards.a4.size(), i = len - 1; i >= 0; i--) {
                    if (CardsAI.getValueByName(SetOfCards.a4.get(i)) > CardsAI.getValue(oppo.get(0))) {
                        list.add(SetOfCards.a4.get(i));
                        break;
                    }
                }
            default:
                break;
        }
        if (list.size() == 0) {
            if (CardsAI.isLessFive())//对手少于5张，可以炸了
            {
                if (SetOfCards.a4.size() > 0) {
                    list.add(SetOfCards.a4.get(SetOfCards.a4.size() - 1));
                    for (String s : list) {
                        to.addAll(CardsAI.getCardsByName(from, s));
                    }
                    return;
                }
            }
            to = null;
        } else {
            for (String s : list) {
                to.addAll(CardsAI.getCardsByName(from, s));
            }
        }
    }

    //主动出牌
    public static void showCards(SetOfCards setOfCards, List<Card> to, List<Card> from) {

        List<String> list = new Vector<String>();
        if (setOfCards.a12345.size() > 0) {
            list.add(setOfCards.a12345.get(setOfCards.a12345.size() - 1));
        }
        // 有单出单 (除开3带，飞机能带的单牌)
        else if ((!CardsAI.isSingleOpper()) && setOfCards.a1.size() > (setOfCards.a111222.size() * 2 + setOfCards.a3.size())
                && CardsAI.getValueByName(setOfCards.a1.get(setOfCards.a1.size() - 1)) < 15) {
            list.add(setOfCards.a1.get(setOfCards.a1.size() - 1));
        } else if (CardsAI.isSingleOpper() && setOfCards.a1.size() > (setOfCards.a111222.size() * 2 + setOfCards.a3.size())) {
            list.add(setOfCards.a1.get(0));
        }
        // 有对子出对子 (除开3带，飞机)
        else if (setOfCards.a2.size() > (setOfCards.a111222.size() * 2 + setOfCards.a3
                .size()) && CardsAI.getValueByName(setOfCards.a2.get(setOfCards.a2.size() - 1)) < 15) {
            list.add(setOfCards.a2.get(setOfCards.a2.size() - 1));
        }
        // 有3带就出3带，没有就出光3
        else if (setOfCards.a3.size() > 0 && CardsAI.getValueByName(setOfCards.a3.get(setOfCards.a3.size() - 1)) < 15) {
            // 3带单,且非关键时刻不能带王，2
            if (setOfCards.a1.size() > 0) {
                list.add(setOfCards.a1.get(setOfCards.a1.size() - 1));
            }// 3带对
            else if (setOfCards.a2.size() > 0) {
                list.add(setOfCards.a2.get(setOfCards.a2.size() - 1));
            }
            list.add(setOfCards.a3.get(setOfCards.a3.size() - 1));
        }// 有双顺出双顺
        else if (setOfCards.a112233.size() > 0) {
            list.add(setOfCards.a112233.get(setOfCards.a112233.size() - 1));
        }// 有飞机出飞机
        else if (setOfCards.a111222.size() > 0) {
            String name[] = setOfCards.a111222.get(0).split(",");
            // 带单
            if (name.length / 3 <= setOfCards.a1.size()) {
                list.add(setOfCards.a111222.get(setOfCards.a111222.size() - 1));
                for (int i = 0; i < name.length / 3; i++)
                    list.add(setOfCards.a1.get(i));
            } else if (name.length / 3 <= setOfCards.a2.size())// 带双
            {
                list.add(setOfCards.a111222.get(setOfCards.a111222.size() - 1));
                for (int i = 0; i < name.length / 3; i++)
                    list.add(setOfCards.a2.get(i));
            }

        } else if ((!CardsAI.isSingleOpper()) && setOfCards.a1.size() > (setOfCards.a111222.size() * 2 + setOfCards.a3.size())) {
            list.add(setOfCards.a1.get(setOfCards.a1.size() - 1));
        } else if (setOfCards.a2.size() > (setOfCards.a111222.size() * 2 + setOfCards.a3
                .size())) {
            list.add(setOfCards.a2.get(setOfCards.a2.size() - 1));
        } else if ( setOfCards.a3.size() > 0&&CardsAI.getValueByName(setOfCards.a3.get(0)) < 15 ) {
            // 3带单,且非关键时刻不能带王，2
            if (setOfCards.a1.size() > 0) {
                list.add(setOfCards.a1.get(setOfCards.a1.size() - 1));
            }// 3带对
            else if (setOfCards.a2.size() > 0) {
                list.add(setOfCards.a2.get(setOfCards.a2.size() - 1));
            }
            list.add(setOfCards.a3.get(setOfCards.a3.size() - 1));
        }
        // 有炸弹出炸弹
        else if (setOfCards.a4.size() > 0) {
            // 4带2,1
            int sizea1 = setOfCards.a1.size();
            int sizea2 = setOfCards.a2.size();
            if (sizea1 >= 2) {
                list.add(setOfCards.a1.get(sizea1 - 1));
                list.add(setOfCards.a1.get(sizea1 - 2));
                list.add(setOfCards.a4.get(0));

            } else if (sizea2 >= 2) {
                list.add(setOfCards.a2.get(sizea1 - 1));
                list.add(setOfCards.a2.get(sizea1 - 2));
                list.add(setOfCards.a4.get(0));

            } else {// 直接炸
                list.add(setOfCards.a4.get(0));

            }
        }
        for (String s : list) {
            to.addAll(CardsAI.getCardsByName(from, s));
        }

    }

    //得到我的最佳的牌
    public static List<Card> getMyBestCards(List<Card> current, List<Card> oppo) {
        //current是我的牌,oppo是对手出的牌,如果为空则我主动出牌
        List<Card> list = new Vector<Card>();

        for (int i = 0, len = current.size(); i < len; i++) {
            Card card = current.get(i);
            if (card.isClicked())
                list.add(card);
        }
        Log.i("mylog", CardsAI.jugdeType(list).toString() + ",");
        CardType myType = CardsAI.jugdeType(list);
        if (oppo == null)//我主动走牌
        {
            Log.i("mylog", "我主动走牌");
            if (myType != CardType.c0)
                return list;
            else {
                return null;
            }
        } else {//我跟牌
            Log.i("mylog", "我跟牌");
            if (CardsAI.checkCards(list, oppo) == 1)//检查是否能出
                return list;
            else {
                return null;
            }
        }

    }

    // 判断牌型
    public static CardType jugdeType(List<Card> list) {
        // 因为之前排序过所以比较好判断
        int len = list.size();
        // 双王,化为对子返回
        if (len == 2 && CardsAI.getColor(list.get(1)) == 5)
            return CardType.c4;
        // 单牌,对子，3不带，4个一样炸弹
        if (len <= 4) { // 如果第一个和最后个相同，说明全部相同
            if (list.size() > 0
                    && CardsAI.getValue(list.get(0)) == CardsAI.getValue(list
                    .get(len - 1))) {
                switch (len) {
                    case 1:
                        return CardType.c1;
                    case 2:
                        return CardType.c2;
                    case 3:
                        return CardType.c3;
                    case 4:
                        return CardType.c4;
                }
            }
            // 当第一个和最后个不同时,3带1
            if (len == 4
                    && ((CardsAI.getValue(list.get(0)) == CardsAI.getValue(list
                    .get(len - 2))) || CardsAI.getValue(list.get(1)) == CardsAI
                    .getValue(list.get(len - 1))))
                return CardType.c31;
            else {
                return CardType.c0;
            }
        }
        // 当5张以上时，连字，3带2，飞机，2顺，4带2等等
        if (len >= 5) {// 现在按相同数字最大出现次数
            Card_index card_index = new Card_index();
            for (int i = 0; i < 4; i++)
                card_index.a[i] = new Vector<Integer>();
            // 求出各种数字出现频率
            CardsAI.getMax(card_index, list); // a[0,1,2,3]分别表示重复1,2,3,4次的牌
            // 3带2 -----必含重复3次的牌
            if (card_index.a[2].size() == 1 && card_index.a[1].size() == 1
                    && len == 5)
                return CardType.c32;
            // 4带2(单,双)
            if (card_index.a[3].size() == 1 && len == 6)
                return CardType.c411;
            if (card_index.a[3].size() == 1 && card_index.a[1].size() == 2
                    && len == 8)
                return CardType.c422;
            // 单连,保证不存在王
            if ((CardsAI.getColor(list.get(0)) != 5)
                    && (card_index.a[0].size() == len)
                    && (CardsAI.getValue(list.get(0))
                    - CardsAI.getValue(list.get(len - 1)) == len - 1))
                return CardType.c123;
            // 连队
            if (card_index.a[1].size() == len / 2
                    && len % 2 == 0
                    && len / 2 >= 3
                    && (CardsAI.getValue(list.get(0))
                    - CardsAI.getValue(list.get(len - 1)) == (len / 2 - 1)))
                return CardType.c1122;
            // 飞机
            if (card_index.a[2].size() == len / 3
                    && (len % 3 == 0)
                    && (CardsAI.getValue(list.get(0))
                    - CardsAI.getValue(list.get(len - 1)) == (len / 3 - 1)))
                return CardType.c111222;
            // 飞机带n单,n/2对
            if (card_index.a[2].size() >= 2 && card_index.a[2].size() == len / 4
                    && ((Integer) (card_index.a[2].get(len / 4 - 1))
                    - (Integer) (card_index.a[2].get(0)) == len / 4 - 1)
                    && len == card_index.a[2].size() * 4)
                return CardType.c11122234;

            // 飞机带n双
            if (card_index.a[2].size() >= 2 && card_index.a[2].size() == len / 5
                    && card_index.a[2].size() == len / 5
                    && ((Integer) (card_index.a[2].get(len / 5 - 1))
                    - (Integer) (card_index.a[2].get(0)) == len / 5 - 1)
                    && len == card_index.a[2].size() * 5)
                return CardType.c1112223344;

        }
        return CardType.c0;
    }

    //设定牌的顺序
    public static void setOrder(List<Card> list) {
        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                // TODO Auto-generated method stub
                int a1 = CardsAI.getColor(o1);// 花色
                int a2 = CardsAI.getColor(o2);
                int b1 = CardsAI.getValue(o1);// 数值
                int b2 = CardsAI.getValue(o2);
                int flag = 0;
                flag = b2 - b1;
                if (flag == 0)
                    return a2 - a1;
                else {
                    return flag;
                }
            }
        });
    }

    //设定顺序后重新设定位置
    // 重新定位 flag代表电脑1 ,2 或者是我0
    public static void rePosition(CardsGameView view, List<Card> list, int flag) {
        if (flag == 1) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Card card = list.get(i);
                int y = view.getScreen_height() - card.getHeight();
                if (card.isClicked())
                    y -= card.getHeight() / 3;
                card.setLocation(view.getScreen_width() / 2 - (len / 2 - i) * card.getWidth() * 2 / 3, y);
            }
        }
        if (flag == 0) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Card card = list.get(i);
                card.setLocation(card.getWidth() / 2, card.getHeight() / 2 + i * card.getHeight() / 7);
            }
        }
        if (flag == 2) {
            for (int i = 0, len = list.size(); i < len; i++) {
                Card card = list.get(i);
                card.setLocation(view.getScreen_width() - 3 * card.getWidth() / 2, card.getHeight() / 2 + i * card.getHeight() / 7);
            }
        }
    }

    // 返回值
    public static int getValue(Card card) {
        int i = Integer.parseInt(card.getName().substring(3, card.getName().length()));
        return i;
    }

    // 返回花色
    public static int getColor(Card card) {
        return Integer.parseInt(card.getName().substring(1, 2));
    }

    // 得到最大相同数
    public static void getMax(Card_index card_index, List<Card> list) {
        int count[] = new int[17];// 1-16各算一种,王算第16种
        for (int i = 0; i < 17; i++)
            count[i] = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (CardsAI.getColor(list.get(i)) == 5)
                count[16]++;
            else
                count[CardsAI.getValue(list.get(i)) - 1]++;
        }
        for (int i = 0; i < 17; i++) {
            switch (count[i]) {
                case 1:
                    card_index.a[0].add(i + 1);
                    break;
                case 2:
                    card_index.a[1].add(i + 1);
                    break;
                case 3:
                    card_index.a[2].add(i + 1);
                    break;
                case 4:
                    card_index.a[3].add(i + 1);
                    break;
            }
        }
    }

    // 检查牌的是否能出
    public static int checkCards(List<Card> c, List<Card> oppo) {
        // 找出当前最大的牌是哪个电脑出的,c是点选的牌
        List<Card> currentlist = oppo;
        CardType cType = CardsAI.jugdeType(c);
        CardType cType2 = CardsAI.jugdeType(currentlist);
        // 如果张数不同直接过滤
        if (cType != CardType.c4 && c.size() != currentlist.size())
            return 0;
        // 比较我的出牌类型
        if (cType != CardType.c4 && cType != cType2) {

            return 0;
        }
        // 比较出的牌是否要大
        // 我是炸弹
        if (cType == CardType.c4) {
            if (c.size() == 2)
                return 1;
            if (cType2 != CardType.c4) {
                return 1;
            }
        }

        // 单牌,对子,3带,4炸弹
        if (cType == CardType.c1 || cType == CardType.c2
                || cType == CardType.c3 || cType == CardType.c4) {
            if (CardsAI.getValue(c.get(0)) <= CardsAI
                    .getValue(currentlist.get(0))) {
                return 0;
            } else {
                return 1;
            }
        }
        // 顺子,连队，飞机裸
        if (cType == CardType.c123 || cType == CardType.c1122
                || cType == CardType.c111222) {
            if (CardsAI.getValue(c.get(0)) <= CardsAI
                    .getValue(currentlist.get(0)))
                return 0;
            else
                return 1;
        }
        // 按重复多少排序
        // 3带1,3带2 ,飞机带单，双,4带1,2,只需比较第一个就行，独一无二的
        if (cType == CardType.c31 || cType == CardType.c32
                || cType == CardType.c411 || cType == CardType.c422
                || cType == CardType.c11122234 || cType == CardType.c1112223344) {
            List<Card> a1 = CardsAI.getOrder2(c); // 我出的牌
            List<Card> a2 = CardsAI.getOrder2(currentlist);// 当前最大牌
            if (CardsAI.getValue(a1.get(0)) < CardsAI.getValue(a2.get(0)))
                return 0;
        }
        return 1;
    }

    // 按照重复次数排序
    public static List getOrder2(List<Card> list) {
        List<Card> list2 = new Vector<Card>(list);
        List<Card> list3 = new Vector<Card>();
        List<Integer> list4 = new Vector<Integer>();
        int len = list2.size();
        int a[] = new int[20];
        for (int i = 0; i < 20; i++)
            a[i] = 0;
        for (int i = 0; i < len; i++) {
            a[CardsAI.getValue(list2.get(i))]++;
        }
        int max = 0;
        for (int i = 0; i < 20; i++) {
            max = 0;
            for (int j = 19; j >= 0; j--) {
                if (a[j] > a[max])
                    max = j;
            }

            for (int k = 0; k < len; k++) {
                if (CardsAI.getValue(list2.get(k)) == max) {
                    list3.add(list2.get(k));
                }
            }
            list2.remove(list3);
            a[max] = 0;
        }
        return list3;
    }

    // 拆对子
    public static void getTwo(List<Card> list, SetOfCards SetOfCards) {
        //List<Card> del = new Vector<Card>();// 要删除的Cards
        // 连续2张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 1 < len
                    && CardsAI.getValue(list.get(i)) == CardsAI.getValue(list
                    .get(i + 1))) {
                String s = list.get(i).getName() + ",";
                s += list.get(i + 1).getName();
                SetOfCards.a2.add(s);
                //for (int j = i; j <= i + 1; j++)
                //	del.add(list.get(j));
                i = i + 1;
            }
        }
        //list.removeAll(del);
    }

    // 拆3带
    public static void getThree(List<Card> list, SetOfCards SetOfCards) {
        //List<Card> del = new Vector<Card>();// 要删除的Cards
        // 连续3张相同
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 2 < len
                    && CardsAI.getValue(list.get(i)) == CardsAI.getValue(list
                    .get(i + 2))) {
                String s = list.get(i).getName() + ",";
                s += list.get(i + 1).getName() + ",";
                s += list.get(i + 2).getName();
                SetOfCards.a3.add(s);
                //for (int j = i; j <= i + 2; j++)
                //del.add(list.get(j));
                i = i + 2;
            }
        }
        //list.removeAll(del);
    }

    // 拆炸弹
    public static void getBoomb(List<Card> list, SetOfCards SetOfCards) {
        //List<Card> del = new Vector<Card>();// 要删除的Cards
        if (list.size() < 1)
            return;
        // 王炸
        if (list.size() >= 2 && CardsAI.getColor(list.get(0)) == 5
                && CardsAI.getColor(list.get(1)) == 5) {
            SetOfCards.a4.add(list.get(0).getName() + "," + list.get(1).getName()); // 按名字加入
            //del.add(list.get(0));
            //del.add(list.get(1));
        }
        // 如果王不构成炸弹咋先拆单
        /*if (CardsAI.getColor(list.get(0)) == 5
                && CardsAI.getColor(list.get(1)) != 5) {
			//del.add(list.get(0));
			SetOfCards.a1.add(list.get(0).getName());
		}*/
        //list.removeAll(del);
        // 一般的炸弹
        for (int i = 0, len = list.size(); i < len; i++) {
            if (i + 3 < len
                    && CardsAI.getValue(list.get(i)) == CardsAI.getValue(list
                    .get(i + 3))) {
                String s = list.get(i).getName() + ",";
                s += list.get(i + 1).getName() + ",";
                s += list.get(i + 2).getName() + ",";
                s += list.get(i + 3).getName();
                SetOfCards.a4.add(s);
                //for (int j = i; j <= i + 3; j++)
                //del.add(list.get(j));
                i = i + 3;
            }
        }
        //list.removeAll(del);
    }

    // 拆双顺
    public static void getTwoTwo(List<Card> list, SetOfCards SetOfCards) {
        //List<String> del = new Vector<String>();// 要删除的Cards
        // 从SetOfCards里面的对子找
        List<String> l = SetOfCards.a2;
        if (l.size() < 3)
            return;
        Integer s[] = new Integer[l.size()];
        for (int i = 0, len = l.size(); i < len; i++) {
            String[] name = l.get(i).split(",");
            s[i] = Integer.parseInt(name[0].substring(3, name[0].length()));
        }
        // s0,1,2,3,4 13,9,8,7,6
        for (int i = 0, len = l.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (s[i] - s[j] == j - i)
                    k = j;
            }
            if (k - i >= 2)// k=4 i=1
            {// 说明从i到k是连队
                String ss = "";
                for (int j = i; j < k; j++) {
                    ss += l.get(j) + ",";
                    //del.add(l.get(j));
                }
                ss += l.get(k);
                SetOfCards.a112233.add(ss);
                //del.add(l.get(k));
                i = k;
            }
        }
        //l.removeAll(del);
    }

    // 拆飞机
    public static void getPlane(List<Card> list, SetOfCards SetOfCards) {
        //List<String> del = new Vector<String>();// 要删除的Cards
        // 从SetOfCards里面的3带找
        List<String> l = SetOfCards.a3;
        if (l.size() < 2)
            return;
        Integer s[] = new Integer[l.size()];
        for (int i = 0, len = l.size(); i < len; i++) {
            String[] name = l.get(i).split(",");
            s[i] = Integer.parseInt(name[0].substring(3, name[0].length()));
        }
        for (int i = 0, len = l.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (s[i] - s[j] == j - i)
                    k = j;
            }
            if (k != i) {// 说明从i到k是飞机
                String ss = "";
                for (int j = i; j < k; j++) {
                    ss += l.get(j) + ",";
                    //del.add(l.get(j));
                }
                ss += l.get(k);
                SetOfCards.a111222.add(ss);
                //del.add(l.get(k));
                i = k;
            }
        }
        //l.removeAll(del);
    }

    // 拆连子
    public static void get123(List<Card> list, SetOfCards SetOfCards) {
        //List<Card> del = new Vector<Card>();// 要删除的Cards
        if (list.size() < 5)
            return;
        // 先要把所有不重复的牌归为一类，防止3带，对子影响
        List<Card> list2 = new Vector<Card>(list);
        List<Card> temp = new Vector<Card>();
        List<Integer> integers = new Vector<Integer>();
        for (Card card : list2) {
            if (integers.indexOf(CardsAI.getValue(card)) < 0 && CardsAI.getColor(card) != 5
                    && CardsAI.getValue(card) != 15) {
                integers.add(CardsAI.getValue(card));
                temp.add(card);
            }
        }
        CardsAI.setOrder(temp);
        for (int i = 0, len = temp.size(); i < len; i++) {
            int k = i;
            for (int j = i; j < len; j++) {
                if (CardsAI.getValue(temp.get(i)) - CardsAI.getValue(temp.get(j)) == j
                        - i) {
                    k = j;
                }
            }
            if (k - i >= 4) {
                String s = "";
                for (int j = i; j < k; j++) {
                    s += temp.get(j).getName() + ",";
                    //del.add(temp.get(j));
                }
                s += temp.get(k).getName();
                //del.add(temp.get(k));
                SetOfCards.a12345.add(s);
                i = k;
            }
        }
        //list.removeAll(del);
    }

    // 拆单牌
    public static void getSingle(List<Card> list, SetOfCards SetOfCards) {
        //List<Card> del = new Vector<Card>();// 要删除的Cards
        // 1
        for (int i = 0, len = list.size(); i < len; i++) {
            SetOfCards.a1.add(list.get(i).getName());
            //del.add(list.get(i));
        }
        CardsAI.delSingle(SetOfCards.a2, SetOfCards);
        CardsAI.delSingle(SetOfCards.a3, SetOfCards);
        CardsAI.delSingle(SetOfCards.a4, SetOfCards);
        CardsAI.delSingle(SetOfCards.a12345, SetOfCards);
        CardsAI.delSingle(SetOfCards.a112233, SetOfCards);
        CardsAI.delSingle(SetOfCards.a111222, SetOfCards);
        //list.removeAll(del);
    }

    //取单
    public static void delSingle(List<String> list, SetOfCards SetOfCards) {
        for (int i = 0, len = list.size(); i < len; i++) {
            String s[] = list.get(i).split(",");
            for (int j = 0; j < s.length; j++)
                SetOfCards.a1.remove(s[j]);
        }
    }

    // 统计各种牌型权值，手数
    public static SetOfCards getBestSetOfCards(List<Card> list2, SetOfCards oldSetOfCards, int[] n) {
        //a4 a3 a2 a12345 a112233 a111222
        SetOfCards temp = new SetOfCards();
        //处理炸弹
        for (int i = 0; i < n[0]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a4.get(i))) {
                temp.a4.add(oldSetOfCards.a4.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a4.get(i)));
            }
        }
        //3带
        for (int i = 0; i < n[1]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a3.get(i))) {
                temp.a3.add(oldSetOfCards.a3.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a3.get(i)));
            }
        }
        //对子
        for (int i = 0; i < n[2]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a2.get(i))) {
                temp.a2.add(oldSetOfCards.a2.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a2.get(i)));
            }
        }
        //顺子
        for (int i = 0; i < n[3]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a12345.get(i))) {
                temp.a12345.add(oldSetOfCards.a12345.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a12345.get(i)));
            }
        }
        //双顺
        for (int i = 0; i < n[4]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a112233.get(i))) {
                temp.a112233.add(oldSetOfCards.a112233.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a112233.get(i)));
            }
        }
        //飞机
        for (int i = 0; i < n[5]; i++) {
            if (CardsAI.isExists(list2, oldSetOfCards.a111222.get(i))) {
                temp.a111222.add(oldSetOfCards.a111222.get(i));
                list2.removeAll(CardsAI.getCardsByName(list2, oldSetOfCards.a111222.get(i)));
            }
        }
        return temp;
    }

    //通过name得到card
    public static List<Card> getCardsByName(List<Card> list, String s) {
        String[] name = s.split(",");
        List<Card> temp = new Vector<Card>();
        int c = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).getName().equals(name[c])) {
                temp.add(list.get(i));
                if (c == name.length - 1)
                    return temp;
                c++;
                i = 0;
            }
        }
        return temp;
    }

    //判断某牌型还存在list不
    public static Boolean isExists(List<Card> list, String s) {
        String name[] = s.split(",");
        int c = 0;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (list.get(i).getName().equals(name[c])) {
                if (c == name.length - 1)
                    return true;
                c++;
                i = 0;
            }

        }

        return false;
    }

    //去除独立牌型
    public static void checkSetOfCards(List<Card> list, SetOfCards SetOfCards1, SetOfCards SetOfCardsSingle) {
        //找出与其他不相关的牌型
        for (int i = 0, len = SetOfCards1.a2.size(); i < len; i++) {
            int flag = 0;
            //Log.i("mylog","..."+ SetOfCards1.a2.get(i));
            String s[] = SetOfCards1.a2.get(i).split(",");
            //flag+=checkSetOfCards_1(SetOfCards1.a2, s);
            flag += checkSetOfCards_1(SetOfCards1.a3, s);
            flag += checkSetOfCards_1(SetOfCards1.a4, s);
            flag += checkSetOfCards_1(SetOfCards1.a112233, s);
            flag += checkSetOfCards_1(SetOfCards1.a111222, s);
            flag += checkSetOfCards_1(SetOfCards1.a12345, s);
            //Log.i("mylog", "a2:flag"+flag);
            if (flag == 0) {
                SetOfCardsSingle.a2.add(SetOfCards1.a2.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a2.get(i)));
            }
        }
        SetOfCards1.a2.removeAll(SetOfCardsSingle.a2);
        for (int i = 0, len = SetOfCards1.a3.size(); i < len; i++) {
            int flag = 0;
            String s[] = SetOfCards1.a3.get(i).split(",");
            flag += checkSetOfCards_1(SetOfCards1.a2, s);
            //flag+=checkSetOfCards_1(SetOfCards1.a3, s);
            flag += checkSetOfCards_1(SetOfCards1.a4, s);
            flag += checkSetOfCards_1(SetOfCards1.a112233, s);
            flag += checkSetOfCards_1(SetOfCards1.a111222, s);
            flag += checkSetOfCards_1(SetOfCards1.a12345, s);
            if (flag == 0) {
                SetOfCardsSingle.a3.add(SetOfCards1.a3.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a3.get(i)));

            }
        }
        SetOfCards1.a3.removeAll(SetOfCardsSingle.a3);
        for (int i = 0, len = SetOfCards1.a4.size(); i < len; i++) {
            int flag = 0;
            String s[] = SetOfCards1.a4.get(i).split(",");
            flag += checkSetOfCards_1(SetOfCards1.a2, s);
            flag += checkSetOfCards_1(SetOfCards1.a3, s);
            //flag+=checkSetOfCards_1(SetOfCards1.a4, s);
            flag += checkSetOfCards_1(SetOfCards1.a112233, s);
            flag += checkSetOfCards_1(SetOfCards1.a111222, s);
            flag += checkSetOfCards_1(SetOfCards1.a12345, s);
            if (flag == 0) {
                SetOfCardsSingle.a4.add(SetOfCards1.a4.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a4.get(i)));
            }
        }
        SetOfCards1.a4.removeAll(SetOfCardsSingle.a4);
        for (int i = 0, len = SetOfCards1.a112233.size(); i < len; i++) {
            int flag = 0;
            String s[] = SetOfCards1.a112233.get(i).split(",");
            flag += checkSetOfCards_1(SetOfCards1.a2, s);
            flag += checkSetOfCards_1(SetOfCards1.a3, s);
            flag += checkSetOfCards_1(SetOfCards1.a4, s);
            //flag+=checkSetOfCards_1(SetOfCards1.a112233, s);
            flag += checkSetOfCards_1(SetOfCards1.a111222, s);
            flag += checkSetOfCards_1(SetOfCards1.a12345, s);
            if (flag == 0) {
                SetOfCardsSingle.a112233.add(SetOfCards1.a112233.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a112233.get(i)));
            }
        }
        SetOfCards1.a112233.removeAll(SetOfCardsSingle.a112233);
        for (int i = 0, len = SetOfCards1.a111222.size(); i < len; i++) {
            int flag = 0;
            String s[] = SetOfCards1.a111222.get(i).split(",");
            flag += checkSetOfCards_1(SetOfCards1.a2, s);
            flag += checkSetOfCards_1(SetOfCards1.a3, s);
            flag += checkSetOfCards_1(SetOfCards1.a4, s);
            flag += checkSetOfCards_1(SetOfCards1.a112233, s);
            //flag+=checkSetOfCards_1(SetOfCards1.a111222, s);
            flag += checkSetOfCards_1(SetOfCards1.a12345, s);
            if (flag == 0) {
                SetOfCardsSingle.a111222.add(SetOfCards1.a111222.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a111222.get(i)));
            }
        }
        SetOfCards1.a111222.removeAll(SetOfCardsSingle.a111222);
        for (int i = 0, len = SetOfCards1.a12345.size(); i < len; i++) {
            int flag = 0;
            String s[] = SetOfCards1.a12345.get(i).split(",");
            flag += checkSetOfCards_1(SetOfCards1.a2, s);
            flag += checkSetOfCards_1(SetOfCards1.a3, s);
            flag += checkSetOfCards_1(SetOfCards1.a4, s);
            flag += checkSetOfCards_1(SetOfCards1.a112233, s);
            flag += checkSetOfCards_1(SetOfCards1.a111222, s);
            //flag+=checkSetOfCards_1(SetOfCards1.a12345, s);
            if (flag == 0) {
                SetOfCardsSingle.a12345.add(SetOfCards1.a12345.get(i));
                list.removeAll(CardsAI.getCardsByName(list, SetOfCards1.a12345.get(i)));
            }
        }
        SetOfCards1.a12345.removeAll(SetOfCardsSingle.a12345);
    }

    public static int checkSetOfCards_1(List<String> list, String[] s) {
        for (int j = 0, len2 = list.size(); j < len2; j++) {
            String ss[] = list.get(j).split(",");
            for (int k = 0; k < ss.length; k++) {
                for (int m = 0; m < s.length; m++) {
                    if (s[m].equals(ss[k])) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    //计算手数
    public static int getTimes(SetOfCards SetOfCards) {
        int count = 0;
        count += SetOfCards.a4.size() + SetOfCards.a3.size() + SetOfCards.a2.size();
        count += SetOfCards.a111222.size() + SetOfCards.a112233.size() + SetOfCards.a12345.size();
        int temp = 0;
        temp = SetOfCards.a1.size() - SetOfCards.a3.size() * 2 - SetOfCards.a4.size() * 3 - SetOfCards.a111222.size() * 3;
        count += temp;
        return count;
    }

    //计算权值   单1 对子2 带3 炸弹10 飞机7 双顺5 顺子4
    public static int getCountValues(SetOfCards SetOfCards) {
        int count = 0;
        count += SetOfCards.a1.size() + SetOfCards.a2.size() * 2 + SetOfCards.a3.size() * 3;
        count += SetOfCards.a4.size() * 10 + SetOfCards.a111222.size() * 7 + SetOfCards.a112233.size() * 5 + SetOfCards.a12345.size() * 4;
        return count;
    }

    //通过name返回值
    public static int getValueByName(String ss) {
        String s[] = ss.split(",");
        return Integer.parseInt(s[0].substring(3, s[0].length()));
    }

    //判断自己是不是地主
    public static Boolean isDizhu() {
        if (CardsAI.currentFlag == CardsAI.dizhuFlag)
            return true;
        else {
            return false;
        }
    }

    //判断对手牌是不是自己的队友
    public static Boolean isFriend() {
        if (CardsAI.isDizhu())
            return false;
        else if (CardsAI.oppoerFlag != CardsAI.dizhuFlag) {
            return true;
        } else {
            return false;
        }
    }

    //如果敌人只有5张牌一下，尽量拆牌
    public static Boolean isLessFive() {
        if (!CardsAI.isFriend() && view.getPlayerList()[CardsAI.oppoerFlag].size() <= 5) {
            return true;
        }
        return false;
    }

    //如果敌人只有一张牌，尽量不出单，出单也从最大的出
    public static Boolean isSingleOpper() {
        if (!CardsAI.isFriend() && view.getPlayerList()[CardsAI.oppoerFlag].size() == 1)
            return true;
        else
            return false;
    }

    public static int getBestDizhuFlag() {
        int count1 = 0, count2 = 0;
        for (int i = 0, len = view.getPlayerList()[0].size(); i < len; i++) {
            if (CardsAI.getValue(view.getPlayerList()[0].get(i)) > 14)
                count1++;
        }
        for (int i = 0, len = view.getPlayerList()[2].size(); i < len; i++) {
            if (CardsAI.getValue(view.getPlayerList()[2].get(i)) > 14)
                count2++;
        }
        if (count1 > count2)
            return 0;
        else {
            return 2;
        }
    }

}

//判断牌型用的
class Card_index {
    List a[] = new Vector[4];// 单张
}