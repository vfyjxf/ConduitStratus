package dev.vfyjxf.conduitstratus.api.data.lang;

import net.minecraft.data.PackOutput;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;

public class LangKeyProvider extends LanguageProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private final MutableList<ILangProvider> providers = Lists.mutable.empty();

    public LangKeyProvider(String modid, PackOutput output) {
        super(output, modid, "en_us");
        for (ModFileScanData data : ModList.get().getAllScanData()) {
            data.getAnnotatedBy(LangProvider.class, ElementType.TYPE)
                    .filter(annotation -> modid.equals(annotation.annotationData().get("value")))
                    .forEach(annotation -> {
                        try {
                            var clazz = Class.forName(annotation.memberName());
                            //if clazz impl ILangProvider
                            if (ILangProvider.class.isAssignableFrom(clazz)) {
                                ILangProvider provider = (ILangProvider) clazz.getDeclaredConstructor().newInstance();
                                providers.add(provider);
                            }
                        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                                 IllegalAccessException |
                                 InstantiationException e) {
                            LOGGER.error("Failed to load lang poster: {}", annotation.memberName(), e);
                        }
                    });
        }
    }

    @Override
    protected void addTranslations() {
        LangBuilder.builders.forEach(builder -> builder.getDefines().forEach(it -> this.add(it.key(), it.value())));
        for (ILangProvider provider : providers) {
            provider.addTranslations(this);
        }
    }
}
