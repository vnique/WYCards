package cn.wydewy.wycards.event;

import android.util.Log;
import android.view.MotionEvent;

import java.util.List;

import cn.wydewy.wycards.controller.CardsAI;
import cn.wydewy.wycards.model.Card;
import cn.wydewy.wycards.view.CardsGameView;

public class EventAction {
	/*
	 * 
	 * */
	MotionEvent event;
	CardsGameView view;

	public EventAction(CardsGameView view, MotionEvent event) {
		this.event = event;
		this.view = view;
	}
	// 操作按钮事件
	public void getButton(){
		if(!view.isHideButton()){
			float x=event.getX(),y=event.getY();
			//左边按钮
			if((x>view.getScreen_width()/2-3*view.getCardWidth())&&(y>view.getScreen_height()-view.getCardHeight()*5/2)&&
					(x<view.getScreen_width()/2-view.getCardWidth())&&(y<view.getScreen_height()-view.getCardHeight()*11/6))
			{
				//抢地主
				if(view.getButtonText()[0].equals("抢地主"))
				{
					//加入地主牌
					for(Card card:view.getDizhuList())
					{
						card.setRear(false);
					}
					view.update();
					view.setTimer(5, 1);
					view.getPlayerList()[1].addAll(view.getDizhuList());
					view.getDizhuList().clear();
					CardsAI.setOrder(view.getPlayerList()[1]);
					CardsAI.rePosition(view, view.getPlayerList()[1], 1);
					view.setDizhuFlag(1);//地主是我;
					CardsAI.dizhuFlag=view.getDizhuFlag();
					view.update();
					view.setTurn(1);
				}
				//出牌
				if(view.getButtonText()[0].equals("出牌"))
				{
					//选出最好的出牌(跟牌和主动出牌)
					List<Card> oppo=null;
					if(view.getOutList()[0].size()<=0&&view.getOutList()[2].size()<=0)
					{
						oppo=null;
					}
					else {
						oppo=(view.getOutList()[0].size()>0)?view.getOutList()[0]:view.getOutList()[2];
					}
					List<Card> mybest=CardsAI.getMyBestCards(view.getPlayerList()[1], oppo);
					//CardsAI.getBestAI(view.getPlayerList()[1],null);
					if(mybest==null)
						return;
					synchronized (view) {
						//加入outlist
						view.getOutList()[1].clear();
						view.getOutList()[1].addAll(mybest);
						//退出playerlist
						view.getPlayerList()[1].removeAll(mybest);
					}
					CardsAI.rePosition(view, view.getPlayerList()[1], 1);
					view.getFlag()[1]=1;
					view.getMessage()[1]="";
					view.nextTurn();
					view.update();
				}
				view.setHideButton(!view.isHideButton());
			}
			//右边
			if(x>view.getScreen_width()/2+view.getCardWidth()&& y>view.getScreen_height()-view.getCardHeight()*5/2&&
					x<view.getScreen_width()/2+3*view.getCardWidth()&&y<view.getScreen_height()-view.getCardHeight()*11/6)
			{
				//不抢
				if(view.getButtonText()[1].equals("不抢"))
				{
					view.setDizhuFlag(CardsAI.getBestDizhuFlag());
					//view.dizhuFlag=0;
					CardsAI.dizhuFlag=view.getDizhuFlag();
					for(Card card:view.getDizhuList())
					{
						//翻开
						card.setRear(false);
					}
					view.update();
					view.Sleep(3000);
					for(Card card:view.getDizhuList())
					{
						card.setRear(true);//关上
					}
					view.getPlayerList()[view.getDizhuFlag()].addAll(view.getDizhuList());
					view.getDizhuList().clear();
					CardsAI.setOrder(view.getPlayerList()[view.getDizhuFlag()]);
					CardsAI.rePosition(view, view.getPlayerList()[view.getDizhuFlag()], view.getDizhuFlag());
					view.update();
					view.setTurn(view.getDizhuFlag());
					view.setHideButton(true);
				}
				//不出
				if(view.getButtonText()[1].equals("不要")){
					if(view.getOutList()[0].size()==0&&view.getOutList()[2].size()==0)
					{
						Log.i("mylog", "不能不不要");
						return;
					}
					Log.i("mylog", "不要");
					view.getMessage()[1]="不要";
					view.setHideButton(true);
					view.nextTurn();
					view.getFlag()[1]=0;
					view.update();
				}
				
			}
		}
	}
	// 取出点击的是哪张牌
	public Card getCard() {
		Card card = null;
		float x = event.getX();// 触摸x坐标
		float y = event.getY();// 触摸y坐标
		float xoffset = view.getCardWidth() * 4 / 5f;
		float yoffset = view.getCardHeight();
		if (y < view.getScreen_height() - 4 * view.getCardHeight() / 3)
			return null;
		for (Card card2 : view.getPlayerList()[1]) {
			if (card2.isClicked()) {
				// 查询符合范围的
				if ((x - card2.getX() > 0)
						&& (y - card2.getY() > 0)
						&& (((x - card2.getX() < xoffset) && (y - card2.getY() < yoffset)) || ((x
								- card2.getX() < card2.getWidth()) && (y - card2.getY() < card2.getHeight() / 3)))) {
					return card2;
				}
			} else {
				// 查询符合范围的
				if ((x - card2.getX() > 0) && (x - card2.getX() < xoffset)
						&& (y - card2.getY() > 0) && (y - card2.getY() < yoffset)) {
					return card2;
				}
			}
		}

		return card;
	}
}
