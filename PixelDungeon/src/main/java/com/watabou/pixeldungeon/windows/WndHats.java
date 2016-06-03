package com.watabou.pixeldungeon.windows;

import com.nyrds.pixeldungeon.items.accessories.Accessory;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.support.Iap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.SystemText;
import com.watabou.noosa.Text;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.ScrollPane;
import com.watabou.pixeldungeon.ui.TextButton;
import com.watabou.pixeldungeon.ui.Window;

import java.util.List;

public class WndHats extends Window {

	private static final int WIDTH = 120;
	private static final int HEIGHT_PORTRAIT = 180;
	private static final int HEIGHT_LANDSCAPE = (int)PixelScene.MIN_HEIGHT_L;
	private static final int MARGIN = 2;
	private static final int BUTTON_HEIGHT = 14;
	public Image slot;

	private int HEIGHT = PixelDungeon.landscape() ? HEIGHT_LANDSCAPE : HEIGHT_PORTRAIT;
	public WndHats() {

		int yPos = 0;

		String equippedName = "";

		if(updateSlotImage()) {equippedName = Accessory.equipped().name();}

		//"Equipped Accessory" slot
		//Title
		Text slotTitle = PixelScene.createMultiline(Game.getVar(R.string.WndHats_SlotTitle) + equippedName, 10);
		slotTitle.hardlight(0xFFFFFF);
		slotTitle.maxWidth(WIDTH - MARGIN * 2);
		slotTitle.measure();
		slotTitle.x = (WIDTH - slotTitle.width()) / 2;
		slotTitle.y = MARGIN;
		add(slotTitle);

		//Image
		slot.setPos(MARGIN, slotTitle.height() + MARGIN * 2);
		add(slot);

		//Unequip Button
		TextButton sb = new RedButton(Game.getVar(R.string.WndHats_UnequipButton)) {
			@Override
			protected void onClick() {
				super.onClick();
				Accessory.unequip();
				updateSlotImage();
			}
		};

		sb.setRect(slot.x + slot.width() * 2 + MARGIN, slot.y , slot.width() * 2, slot.height() / 2 );

		add(sb);

		//List of Accessories
		//Title
		Text listTitle = PixelScene.createMultiline(Game.getVar(R.string.WndHats_ListTitle), 10);
		listTitle.hardlight(TITLE_COLOR);
		listTitle.maxWidth(WIDTH - MARGIN * 2);
		listTitle.measure();
		listTitle.x = (WIDTH - listTitle.width()) / 2;
		listTitle.y = slot.y + slot.height() + MARGIN * 2;

		add(listTitle);

		List<String> hats = Accessory.getAccessoriesList();

		Component content = new Component();

		//List
		for (final String item: hats) {
			String price =  "$ 999";// Iap.getSkuPrice(item);
			if(price!=null) {

				Accessory accessory = Accessory.getByName(item);

				if(accessory.haveIt()) {
					price = Game.getVar(R.string.WndHats_Purchased);
				}

				//Image
				Image hat = accessory.getImage();
				hat.setPos(0,yPos);
				content.add(hat);

				String hatText = Accessory.getByName(item).name() + "\n" + Accessory.getByName(item).desc();

				//Text
				Text info = PixelScene.createMultiline(hatText, 10 );

				info.hardlight(0xFFFFFF);
				info.x = hat.x + hat.width() + MARGIN;
				info.y = hat.y;
				info.maxWidth(WIDTH - (int)hat.width() - MARGIN);
				info.measure();

			    content.add(info);

				//Pricetag
				SystemText priceTag = new SystemText(12);
				priceTag.text(price);

				priceTag.hardlight(0xFFFF00);
				priceTag.x = hat.x;
				priceTag.y = hat.y + hat.height();
				priceTag.maxWidth((int)hat.width());
				priceTag.measure();

				content.add(priceTag);

				String buttonText = Game.getVar(R.string.WndHats_InfoButton);
				final Accessory finalAccessory = accessory;

				if(accessory.haveIt()) {
					buttonText = Game.getVar(R.string.WndHats_EquipButton);
				}

				//Examine Button
				final String finalPrice = price;
				TextButton rb = new RedButton(buttonText) {
					@Override
					protected void onClick() {
						super.onClick();

						if(finalAccessory.haveIt()) {
							finalAccessory.equip();
							Dungeon.hero.updateLook();
							updateSlotImage();
							onBackPressed();
							return;
						}
						GameScene.show( new WndHatInfo(item, finalPrice) );
					}
				};

				rb.setRect(info.x, info.y + info.height() + MARGIN * 2, WIDTH - hat.width() - MARGIN, BUTTON_HEIGHT );

			    content.add(rb);
				yPos += rb.height() + info.height() + MARGIN * 9;
			}
			else{
				//"No connection" Message
				Text info = PixelScene.createMultiline( Game.getVar(R.string.WndHats_NoConnectionMsg), 10 );

				info.hardlight(0xFFFFFF);
				info.x = MARGIN;
				info.y = yPos;
				info.maxWidth(WIDTH - MARGIN * 2);
				info.measure();

				content.add(info);
				yPos = yPos * 5;
				break;
			}
		}

		int h = Math.min(HEIGHT - MARGIN, yPos);

		float topGap = listTitle.y + listTitle.height() + MARGIN;
		float BottomGap = slotTitle.height() + slot.height() + listTitle.height() + MARGIN * 5;

		resize( WIDTH,  h);

		content.setSize(WIDTH, yPos);
		ScrollPane list = new ScrollPane(content);
		list.dontCatchTouch();

		add(list);

		list.setRect(0, topGap, WIDTH, HEIGHT - BottomGap);

	}

	public boolean updateSlotImage(){
		if(Accessory.equipped()!=null){
			slot = Accessory.equipped().getImage();
			return true;
		}
		else{
			slot = Accessory.getSlotImage();
			return false;
		}
	}
}