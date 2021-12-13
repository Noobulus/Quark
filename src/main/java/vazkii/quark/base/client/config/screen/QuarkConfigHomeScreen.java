package vazkii.quark.base.client.config.screen;

import org.apache.commons.lang3.text.WordUtils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import vazkii.quark.api.config.IConfigCategory;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.config.IngameConfigHandler;
import vazkii.quark.base.client.config.external.ExternalConfigHandler;
import vazkii.quark.base.client.config.screen.widgets.CheckboxButton;
import vazkii.quark.base.client.config.screen.widgets.IconButton;
import vazkii.quark.base.client.config.screen.widgets.SocialButton;
import vazkii.quark.base.handler.ContributorRewardHandler;
import vazkii.quark.base.module.ModuleCategory;

public class QuarkConfigHomeScreen extends AbstractQScreen {

	private static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation(Quark.MOD_ID, "textures/misc/panorama/panorama"));
	private static final PanoramaRenderer PANORAMA = new PanoramaRenderer(CUBE_MAP);
	float time;

	public QuarkConfigHomeScreen(Screen parent) {
		super(parent);
	}

	@Override
	protected void init() {
		super.init();

		final int perLine = 3;
		boolean addExternal = ExternalConfigHandler.instance.hasAny();
		
		int pad = 10;
		int vpad = 23;
		int bWidth = 120;
		int left = width / 2 - ((bWidth + pad) * perLine / 2) + 4;
		int vStart = 70; 

		int i = 0;
		int catCount = ModuleCategory.values().length + 1;
		if(addExternal)
			catCount++;
		
		boolean shiftedLeft = false;
		int useLeft = left;
		
		for(ModuleCategory category : ModuleCategory.values()) {
			if(!shiftedLeft && catCount - i < perLine) {
				useLeft = width / 2 - ((bWidth + pad) * (catCount - i) / 2);
				shiftedLeft = true;
			}
			
			int x = useLeft + (bWidth + pad) * (i % perLine);
			int y = vStart + (i / perLine) * vpad;

			IConfigCategory configCategory = IngameConfigHandler.INSTANCE.getConfigCategory(category);
			Component comp = componentFor(configCategory);

			Button icon = new IconButton(x, y, bWidth - 20, 20, comp, new ItemStack(category.item), categoryLink(configCategory));
			Button checkbox = new CheckboxButton(x + bWidth - 20, y, IngameConfigHandler.INSTANCE.getCategoryEnabledObject(category)); 

			addRenderableWidget(icon);
			addRenderableWidget(checkbox);

			if(category.requiredMod != null && !ModList.get().isLoaded(category.requiredMod)) {
				icon.active = false;
				checkbox.active = false;
			}

			i++;
		}
		
		IConfigCategory cat = IngameConfigHandler.INSTANCE.getConfigCategory(null);
		addRenderableWidget(new Button(useLeft + (bWidth + pad) * (i % perLine), vStart + (i / perLine) * vpad, bWidth, 20, componentFor(cat), categoryLink(cat)));
		i++;
		
		if(addExternal) {
			cat = ExternalConfigHandler.instance.mockCategory;
			addRenderableWidget(new Button(useLeft + (bWidth + pad) * (i % perLine), vStart + (i / perLine) * vpad, bWidth, 20, componentFor(cat), categoryLink(cat)));
		}

		bWidth = 200;
		addRenderableWidget(new Button(width / 2 - bWidth / 2, height - 30, bWidth, 20, new TranslatableComponent("quark.gui.config.save"), this::commit));

		vStart = height - 55;
		bWidth = 20;
		pad = 5;
		left = (width - (bWidth + pad) * 5) / 2;
		addRenderableWidget(new SocialButton(left, vStart, new TranslatableComponent("quark.gui.config.social.website"), 0x48ddbc, 0, webLink("https://quarkmod.net")));
		addRenderableWidget(new SocialButton(left + bWidth + pad, vStart,new TranslatableComponent("quark.gui.config.social.discord"), 0x7289da, 1, webLink("https://discord.gg/vm")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 2, vStart, new TranslatableComponent("quark.gui.config.social.patreon"), 0xf96854, 2, webLink("https://patreon.com/vazkii")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 3, vStart, new TranslatableComponent("quark.gui.config.social.reddit"), 0xff4400, 3, webLink("https://reddit.com/r/quarkmod")));
		addRenderableWidget(new SocialButton(left + (bWidth + pad) * 4, vStart, new TranslatableComponent("quark.gui.config.social.twitter"), 0x1da1f2, 4, webLink("https://twitter.com/VazkiiMods")));
	}

	private static Component componentFor(IConfigCategory c) {
		TranslatableComponent comp = new TranslatableComponent("quark.category." + c.getName());

		if(c.isDirty())
			comp.append(new TextComponent("*").withStyle(ChatFormatting.GOLD));

		return comp;
	}

	public void commit(Button button) {
		IngameConfigHandler.INSTANCE.commit();
		returnToParent(button);
	}

	@Override
	public void render(PoseStack mstack, int mouseX, int mouseY, float pticks) {
		time += pticks;
		PANORAMA.render(pticks, 0.85F);
		
		int boxWidth = 400;
		fill(mstack, width / 2 - boxWidth / 2, 0, width / 2 + boxWidth / 2, this.height, 0x66000000);
		fill(mstack, width / 2 - boxWidth / 2 - 1, 0, width / 2 - boxWidth / 2, this.height, 0x66999999); // nice
		fill(mstack, width / 2 + boxWidth / 2, 0, width / 2 + boxWidth / 2 + 1, this.height, 0x66999999);

		ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		blit(mstack, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);

		super.render(mstack, mouseX, mouseY, pticks);

		drawCenteredString(mstack, font, ChatFormatting.BOLD + I18n.get("quark.gui.config.header", WordUtils.capitalizeFully(Quark.MOD_ID)), width / 2, 15, 0x48ddbc);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader1", ChatFormatting.LIGHT_PURPLE, ContributorRewardHandler.featuredPatron, ChatFormatting.RESET), width / 2, 28, 0x9EFFFE);
		drawCenteredString(mstack, font, I18n.get("quark.gui.config.subheader2"), width / 2, 38, 0x9EFFFE);
	}

}
