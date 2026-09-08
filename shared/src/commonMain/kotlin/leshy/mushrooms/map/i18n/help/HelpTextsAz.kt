package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Azərbaycanca — kömək ekranları, `.claude/plans/help-screens.md`. */
internal val azerbaijaniHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Tətbiqin əsas ekranı: gəzinti burada yazılır. GPS ilə iziniz yazılır, hər tapıntı isə " +
            "koordinatları və vaxtı ilə saxlanılır — özü də dərhal, ona görə gəzintini istənilən anda " +
            "kəsmək olar, yazılanlar itmir.",
    HelpKey.RecordStartFinish to
        "«Başla» ad soruşur və yazmağa başlayır; sonra düymə «Fasilə» olur, fasilədə isə «Davam et» və " +
            "«Bitir» görünür. «Bitir» gəzintini bağlayır və onu «Gəzinti arxivi»nə köçürür.",
    HelpKey.RecordTiles to
        "Aşağıdakı göbələk lövhəcikləri tapıntıların qeyd olunduğu yerdir: «+» indiki nöqtənizdə tapıntı " +
            "qoyur, «−» həmin növün son səhv qeydini götürür. «+» üzərinə uzun basmaq bir neçə ədədi " +
            "birdən daxil etməyi açır; bir gəzintidə eyni göbələkdən 999-dan çoxunu qeyd etmək olmaz.",
    HelpKey.RecordPlace to
        "Soldakı dairəvi düymə yer qeyd edir — ad, təsvir və fotoşəkil ilə. Yer indi durduğunuz nöqtəyə " +
            "qoyulur və gəzintidən sonra da xəritədə qalır.",
    HelpKey.RecordNavigation to
        "Yer nişanına uzun basmaq ora naviqasiyanı işə salır: sağ yuxarıdakı panel hədəfə istiqaməti və " +
            "məsafəni göstərir. Paneldəki çarpaz naviqasiyanı söndürür.",
    HelpKey.RecordSearchAndOwn to
        "Sağdakı lupa göbələyi adına görə tapır və lövhəciyini lentin əvvəlinə keçirir — çox növ açıq " +
            "olanda bu daha sürətlidir. Lentin artı işarəli son lövhəciyi kataloqda olmayan öz növünüzü " +
            "əlavə edir.",
    HelpKey.RecordFilters to
        "Sol yuxarıdakı «Filtrlər» düyməsi hansı növlərin və hansı dövrün tapıntılarının xəritədə " +
            "görünəcəyini müəyyən edir, üzərindəki rəqəm isə indi neçə filtrin açıq olduğunu bildirir. " +
            "Filtr «Tapıntı xəritəsi» ilə ortaqdır: burada açdığınız orada da işləyir.",
    HelpKey.RecordBackground to
        "İzin yazılması tətbiq arxa planda olanda da davam edir. Android-də gedən gəzinti «+»/«−» " +
            "düymələri olan bildiriş kimi də görünür — tapıntını telefonun kilidini açmadan qeyd " +
            "etmək olar. Cari gəzintidən başqa, xəritə keçmiş gəzintilərin tapıntılarını və qeyd " +
            "olunmuş yerlərini göstərir — onlardan hara getdiyiniz və orada nə olduğu görünür.",
    HelpKey.ArchivePurpose to
        "Bütün gəzintiləriniz, yeniləri yuxarıda. Kartda ad, tarix, müddət, kilometr, tapıntı sayı və " +
            "keçilmiş izin kiçik təsviri var.",
    HelpKey.ArchiveDetail to
        "Karta toxunmaq gəzintini bütövlükdə açır: statistika, növlərə görə tapıntılar, qeyd olunmuş " +
            "yerlər, təsvir və «Xəritəyə bax» düyməsi. Adı və təsviri elə oradaca dəyişmək olar.",
    HelpKey.ArchiveShare to
        "«Paylaş» düyməsi gəzintinin işarələdiyiniz hissələrindən şəkil yığır. Gəzintinin xəritəsini " +
            "göndərməzdən əvvəl yadda saxlayın: ondan göbələkləri məhz harada tapdığınız görünür.",
    HelpKey.ArchiveSelection to
        "Karta uzun basmaq seçim rejimini açır: istədiyiniz gəzintiləri toxunmaqla işarələyin və " +
            "«Gəzintiləri sil» düyməsini basın; «Geri» düyməsi bu rejimdən çıxır. Silmək geri qaytarılmır " +
            "— gəzinti ilə birlikdə onun izi, tapıntıları, qeyd olunmuş yerləri və fotoları da yox olur.",
    HelpKey.ArchiveUnfinished to
        "Bitməmiş gəzinti də siyahıda görünür: bitmə vaxtı yerinə onda «davam edir» yazılır. Belə " +
            "gəzintinin hələ müddəti yoxdur, ona görə «Tapıntı xəritəsi»ndəki ümumi vaxta daxil olmur.",
    HelpKey.MapPurpose to
        "Ümumi xəritə: bütün gəzintilərinizin tapıntıları, izləri və qeyd olunmuş yerləri bir kətan " +
            "üzərində birdən. Ümumi mənzərəni görmək üçündür — göbələk yerləriniz haradadır və ildən-ilə " +
            "necə dəyişir.",
    HelpKey.MapFullScreen to
        "Yuxarıda bütün tapıntıları birdən göstərən xəritə var; ona toxunmaq xəritəni tam ekranda açır. " +
            "Tapıntı çox olanda yaxın nişanlar rəqəmli dairəyə yığılır — xəritəni yaxınlaşdırın, o " +
            "ayrı-ayrı göbələklərə dağılacaq. Göbələk nişanlarının ölçüsü «Parametrlər»dən tənzimlənir.",
    HelpKey.MapSliders to
        "Xəritənin altında iki sürüşdürücü var — tarix aralığı və mövsüm, yəni ay aralığı — və onlardan " +
            "aşağıdakı hər şey seçimə görə hesablanır. Sürüşdürücülər yalnız bir gündən çox gündə " +
            "gəzintiniz olanda görünür.",
    HelpKey.MapStats to
        "Sürüşdürücülərin altında: neçə gəzinti, kilometr, vaxt və tapıntı olduğu, növlərə görə " +
            "lövhəciklər və dairəvi diaqram. Ümumi vaxt bitmiş gəzintilərdən yığılır: bitməmişin hələ " +
            "müddəti yoxdur.",
    HelpKey.MapFilters to
        "«Filtrlər» düyməsi tam ekran xəritədə, sol yuxarıda yaşayır: orada həmin iki ox, növlərin " +
            "siyahısı və keçmiş izlərin göstərilməsi açarı var. Düymədəki rəqəm neçə filtrin açıq " +
            "olduğunu bildirir; filtr yazma ekranı ilə ortaqdır.",
    HelpKey.MapPlaces to
        "Yer nişanına toxunmaq onun fotoşəkilli və təsvirli kartını açır. Elə oradan yeri dəyişmək və ya " +
            "silmək olar.",
    HelpKey.SpeciesPurpose to
        "Burada yazma ekranında hansı göbələklərin lövhəcik olacağına qərar verirsiniz. Kataloq ölkələr " +
            "üzrə kolleksiyalara bölünüb, yanında isə kataloqda olmayan növlər yaşayır — onları özünüz " +
            "əlavə edirsiniz.",
    HelpKey.SpeciesCollections to
        "«Göbələk kolleksiyaları»nda ölkənin sətrinə toxunmaq onun növlərini açır: ölkənin " +
            "yanındakı işarə bütün kolleksiyanı açır, içəridəki işarələr ayrı-ayrı növləri. " +
            "Yuxarıdakı axtarış sahəsi adına görə həm ölkəni, həm də ayrıca göbələyi tapır.",
    HelpKey.SpeciesOwn to
        "«Əlavə edilmiş göbələklər»də «Göbələk əlavə et» düyməsi forma açır: ad, elmi ad, nişanın " +
            "rəngi və şəkil — kameradan, qalereyadan və ya kataloqdan. Ardınca tətbiq «Hansı " +
            "kolleksiyaya?» soruşur: öz adınız belə göbələkləri bir yerə yığır, boş sahə isə " +
            "«Digərləri»nə salır. Karandaş artıq əlavə edilmiş növü dəyişir, çarpaz onu silir.",
    HelpKey.SpeciesCheckboxes to
        "İşarəni götürmək heç nəyi silmir — növ sadəcə lövhəcik kimi görünmür, keçmiş tapıntılar yerində " +
            "qalır. Öz növünüzü silmək isə geri qaytarılmır: onun keçmiş gəzintilərdəki bütün qeydləri " +
            "«Naməlum göbələk»ə keçir. «arxivdən» yazılı növlər idxal edilmiş gəzintilərlə gəlib.",
    HelpKey.SpeciesImages to
        "Tətbiqdəki bütün göbələk şəkilləri şərtidir: lövhəciyi tanımağa kömək edir, meşədəki göbələyi " +
            "yox. Naməlum göbələkləri onlara görə təyin etməyin.",
    HelpKey.PreparationPurpose to
        "Xəritə parçalarını əvvəlcədən telefonun yaddaşına yükləyir ki, meşədə internet olmadan xəritə " +
            "yerində qalsın: bu olmasa, rabitədən uzaqda xəritənin yerində boş fon olacaq.",
    HelpKey.PreparationDownload to
        "Lazım olan sahəni tapın — xəritəni hərəkət etdirin və miqyasını dəyişin — sonra sağ aşağıdakı " +
            "aşağı oxlu dairəvi düyməni basın. Tətbiq indi ekranda olanın nə qədər yer tutacağını " +
            "göstərir: «Bu ərazini yüklə» ad soruşur və yükləməni başladır, «Ləğv et» xəritəni qaytarır.",
    HelpKey.PreparationRegions to
        "Yüklənmiş ərazilər aşağıda zolaq şəklində durur. Lövhəyə toxunmaq xəritədə həmin əraziyə uçur, " +
            "lövhədəki düymələr isə yükləməni fasiləyə verir və davam etdirir, xətadan sonra cəhdi " +
            "təkrarlayır və ərazini silir.",
    HelpKey.PreparationAreaSize to
        "Ekranda görünən nə varsa, məhz o yüklənir, ona görə xəritəni hərəkət etdirdikcə həcm " +
            "qiymətləndirməsi dəyişir. Ərazi böyüdükcə onu daha az təfərrüatlı etmək lazım gəlir — bir " +
            "nəhəng ərazidənsə bir neçə kiçik sahəni yükləmək sərfəlidir. Ərazi adları " +
            "təkrarlanmamalıdır.",
    HelpKey.PreparationBackground to
        "Yükləmə arxa planda gedir və ekrandan çıxsanız da kəsilmir, fasilədə isə gediş saxlanılır. " +
            "«Parametrlər»dəki «Xəritə məlumatlarını yenilə» bütün saxlanmış əraziləri yenidən yükləyir.",
    HelpKey.DataPurpose to
        "Gəzintilərin telefonlar arasında köçürülməsi və ehtiyat nüsxə: seçilmiş gəzintilər bir arxiv " +
            "faylına yazılır, belə faylı isə geri yükləmək olar — bu və ya başqa cihazda.",
    HelpKey.DataExport to
        "Yuxarıdakı açar «İxrac» və ya «İdxal» seçir. «İxrac»da arxivin adını verin, gəzinti seçimi " +
            "sətrinə toxunub lazım olanları işarələyin, sonra «Hazırdır» — telefon faylı hara saxlamağı " +
            "soruşacaq.",
    HelpKey.DataImport to
        "«İdxal»da «Fayl seç» düyməsini basın, istəsəniz yüklənən gəzintilərin adlarına əlavə olunacaq " +
            "bir qeyd yazın və «Hazırdır» düyməsini basın; arxiv oxunanda «Arxivə» düyməsi görünəcək. " +
            "«Ləğv et» düyməsi yazılanları heç nə saxlamadan təmizləyir.",
    HelpKey.DataArchiveContents to
        "Arxivə iz, tapıntılar, qeyd olunmuş yerlər, fotolar və kataloqda olmayan göbələk növləri düşür — " +
            "digər cihazda onlar «Əlavə edilmiş göbələklər»də «arxivdən» yazısı ilə görünür.",
    HelpKey.DataDuplicates to
        "İdxal gəzintiləri həmişə mövcud olanların yanına əlavə edir və heç nəyi əvəz etmir, ona görə " +
            "eyni faylı təkrar yükləmək onları bir daha yaradacaq: adlara əlavə sonradan onları ayırd " +
            "etməyə kömək edir. Sonda neçə gəzintinin yükləndiyi və neçəsinin oxunmadığı göstərilir.",
    HelpKey.SettingsPurpose to
        "Tətbiqin ümumi parametrləri: interfeys dili, görünüş, yazma ekranındakı göbələk lövhəciklərinin " +
            "görkəmi və sırası, bir də xəritənin baxımı.",
    HelpKey.SettingsLanguage to
        "«İnterfeys dili» sətri dillərin siyahısını açır: toxunuş dili seçir, yuxarıdakı işarə seçimi " +
            "təsdiqləyir, ox heç nə dəyişmədən çıxır. Dil bütün tətbiqdə dərhal tətbiq olunur, yenidən " +
            "başlatmaq lazım deyil.",
    HelpKey.SettingsTheme to
        "«Görünüş» tətbiqin işıqlı və qaranlıq mövzusunu dəyişir. «Sistem» seçimi telefona buraxır: " +
            "tətbiq onunla birlikdə qaralır və işıqlanır.",
    HelpKey.SettingsMushroomSize to
        "Sürüşdürücü xəritədəki göbələk nişanlarının ölçüsünü təyin edir — həm yazma ekranında, həm də " +
            "ümumi «Tapıntı xəritəsi»ndə. Altındakı şəkil siz dartarkən dəyişir, beləcə ölçünü " +
            "buraxmazdan əvvəl görürsünüz.",
    HelpKey.SettingsMushroomOrder to
        "Adətən yenicə qeyd olunmuş göbələklər lövhəcik lentinin əvvəlinə qalxır. «Göbələk sırasını " +
            "sabitlə» bunu tamamilə söndürür, «Gəzinti sonunda göbələk sırasını sıfırla» isə gəzinti " +
            "bitəndə ilkin sıranı qaytarır.",
    HelpKey.SettingsMapData to
        "«Xəritə məlumatlarını yenilə» xəritənin serverdə dəyişib-dəyişmədiyini yoxlayır və dəyişibsə, " +
            "saxlanmış bütün oflayn əraziləri yenidən yükləyir. «Xəritə keşini təmizlə» yalnız baxarkən " +
            "yüklənəni silir — «Öncədən yükləmə»dəki ərazilər yerində qalır.",
)
