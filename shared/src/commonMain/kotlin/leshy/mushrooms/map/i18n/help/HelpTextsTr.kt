package leshy.mushrooms.map.i18n.help

import leshy.mushrooms.map.i18n.HelpKey

/** Türkçe — yardım ekranları, `.claude/plans/help-screens.md`. */
internal val turkishHelpTexts: Map<HelpKey, String> = mapOf(
    HelpKey.RecordPurpose to
        "Uygulamanın ana ekranı: yürüyüş burada kaydedilir. GPS ile iziniz yazılır, her bulgu ise " +
            "koordinatları ve saatiyle kaydedilir — hem de hemen, bu yüzden yürüyüş istediğiniz anda " +
            "kesilebilir, kaydedilenler kaybolmaz.",
    HelpKey.RecordStartFinish to
        "«Başlat» bir ad sorar ve kaydı başlatır; ardından düğme «Duraklat» olur, duraklatıldığında ise " +
            "«Devam et» ve «Bitir» görünür. «Bitir» yürüyüşü kapatır ve «Yürüyüş Arşivi»ne taşır.",
    HelpKey.RecordTiles to
        "Alttaki mantar kutucukları bulguların işaretlendiği yerdir: «+» bulunduğunuz noktaya bir bulgu " +
            "ekler, «−» o türün son yanlış işaretini kaldırır. «+» düğmesine uzun basmak birden çok adedi " +
            "aynı anda girmeyi açar; bir yürüyüşte aynı mantardan 999’dan fazlası işaretlenemez.",
    HelpKey.RecordPlace to
        "Soldaki yuvarlak düğme bir yer işaretler — ad, açıklama ve fotoğrafla. Yer, şu anda durduğunuz " +
            "noktaya konur ve yürüyüşten sonra da haritada kalır.",
    HelpKey.RecordNavigation to
        "Bir yer işaretine uzun basmak oraya yönlendirmeyi açar: sağ üstteki panel hedefe yönü ve " +
            "uzaklığı gösterir. Paneldeki çarpı yönlendirmeyi kapatır.",
    HelpKey.RecordSearchAndOwn to
        "Sağdaki büyüteç mantarı adıyla bulur ve kutucuğunu şeridin başına taşır — çok sayıda tür açıkken " +
            "bu daha hızlıdır. Şeridin artı işaretli son kutucuğu, katalogda olmayan kendi türünüzü " +
            "ekler.",
    HelpKey.RecordFilters to
        "Sol üstteki «Filtreler» düğmesi hangi türlerin ve hangi dönemin bulgularının haritada " +
            "görüneceğini belirler; üzerindeki sayı şu anda kaç filtrenin açık olduğunu söyler. Filtre " +
            "«Bulgu Haritası» ile ortaktır: burada açtığınız orada da geçerlidir.",
    HelpKey.RecordBackground to
        "İz kaydı, uygulama arka plandayken de sürer. Güncel yürüyüşün yanı sıra harita geçmiş " +
            "yürüyüşlerin bulgularını ve işaretlenmiş yerlerini gösterir — onlardan nerede yürüdüğünüz ve " +
            "orada ne olduğu görülür.",
    HelpKey.ArchivePurpose to
        "Tüm yürüyüşleriniz, yeniler üstte. Kartta ad, tarih, süre, kilometre, bulgu sayısı ve yürünen " +
            "izin küçük görseli bulunur.",
    HelpKey.ArchiveDetail to
        "Karta dokunmak yürüyüşü bütünüyle açar: istatistikler, türlere göre bulgular, işaretlenmiş " +
            "yerler, açıklama ve «Haritayı görüntüle» düğmesi. Ad ile açıklama da aynı yerde " +
            "değiştirilir.",
    HelpKey.ArchiveShare to
        "«Paylaş» düğmesi, yürüyüşün işaretlediğiniz bölümlerinden bir görsel oluşturur. Bir yürüyüşün " +
            "haritasını göndermeden önce unutmayın: mantarları tam olarak nerede bulduğunuz görünür.",
    HelpKey.ArchiveSelection to
        "Karta uzun basmak seçim kipini açar: istediğiniz yürüyüşleri dokunarak işaretleyin ve " +
            "«Yürüyüşleri sil» düğmesine basın; «Geri» düğmesi bu kipten çıkar. Silme geri alınamaz — " +
            "yürüyüşle birlikte izi, bulguları, işaretlenmiş yerleri ve fotoğrafları da gider.",
    HelpKey.ArchiveUnfinished to
        "Bitmemiş yürüyüş de listede görünür: bitiş saati yerine «devam ediyor» yazar. Böyle bir " +
            "yürüyüşün henüz süresi yoktur, bu yüzden «Bulgu Haritası»ndaki toplam süreye girmez.",
    HelpKey.MapPurpose to
        "Toplu harita: tüm yürüyüşlerinizin bulguları, izleri ve işaretlenmiş yerleri tek bir yüzeyde bir " +
            "arada. Bütünü görmek için vardır — mantar yerleriniz nerede ve yıldan yıla nasıl değişiyor.",
    HelpKey.MapFullScreen to
        "Üstte tüm bulguları bir arada gösteren harita var; ona dokunmak haritayı tam ekran açar. " +
            "Bulgular çoksa yakın işaretler sayılı bir daire hâlinde toplanır — haritayı yakınlaştırın, " +
            "tek tek mantarlara ayrılır. Mantar simgelerinin boyutu «Ayarlar»dan ayarlanır.",
    HelpKey.MapSliders to
        "Haritanın altında iki kaydırıcı var — tarih aralığı ve mevsim, yani ay aralığı — ve altındaki " +
            "her şey seçime göre hesaplanır. Kaydırıcılar ancak birden çok güne ait yürüyüşünüz olduğunda " +
            "görünür.",
    HelpKey.MapStats to
        "Kaydırıcıların altında: kaç yürüyüş, kilometre, süre ve bulgu olduğu, türlere göre kutucuklar ve " +
            "pasta grafik. Toplam süre biten yürüyüşlerden toplanır: devam edenin henüz süresi yoktur.",
    HelpKey.MapFilters to
        "«Filtreler» düğmesi tam ekran haritada, sol üstte durur: orada aynı iki eksen, tür listesi ve " +
            "geçmiş izlerin gösterimi anahtarı vardır. Düğmedeki sayı kaç filtrenin açık olduğunu söyler; " +
            "filtre kayıt ekranıyla ortaktır.",
    HelpKey.MapPlaces to
        "Bir yer işaretine dokunmak, fotoğrafı ve açıklamasıyla kartını açar. Yer oradan değiştirilebilir " +
            "ya da silinebilir.",
    HelpKey.SpeciesPurpose to
        "Kayıt ekranında hangi mantarların kutucuk olacağına burada karar verirsiniz. Katalog ülkelere " +
            "göre koleksiyonlara ayrılmıştır, yanında ise katalogda bulunmayan türler yaşar — onları siz " +
            "eklersiniz.",
    HelpKey.SpeciesCollections to
        "«Mantar koleksiyonları» içinde bir ülkenin satırına dokunmak türlerini açar: ülkenin yanındaki " +
            "onay kutusu koleksiyonun tamamını açar, içindekiler tek tek türleri. Üstteki arama alanı " +
            "ülkeyi adıyla bulur.",
    HelpKey.SpeciesOwn to
        "«Eklenen mantarlar» bölümünde «Mantar ekle» düğmesi bir form açar: ad, bilimsel ad, işaret rengi " +
            "ve görsel — kameradan, galeriden ya da katalogdan. Kalem eklenmiş bir türü değiştirir, çarpı " +
            "onu siler.",
    HelpKey.SpeciesCheckboxes to
        "İşareti kaldırmak hiçbir şeyi silmez — tür yalnızca kutucuk olarak görünmez olur, geçmiş " +
            "bulgular yerinde kalır. Kendi türünüzü silmek ise geri alınamaz: geçmiş yürüyüşlerdeki tüm " +
            "işaretleri «Bilinmeyen mantar»a geçer. «arşivden» etiketli türler içe aktarılan yürüyüşlerle " +
            "gelmiştir.",
    HelpKey.SpeciesImages to
        "Uygulamadaki tüm mantar görselleri temsilîdir: kutucuğu tanımaya yardım eder, ormandaki mantarı " +
            "değil. Bilinmeyen mantarları onlara bakarak belirlemeyin.",
    HelpKey.PreparationPurpose to
        "Harita parçalarını önceden telefonun belleğine indirir ki ormanda internet olmadan harita " +
            "yerinde kalsın: bunsuz, kapsama dışında haritanın yerinde boş bir zemin olur.",
    HelpKey.PreparationDownload to
        "İstediğiniz alanı bulun — haritayı kaydırın ve yakınlaştırın — sonra sağ alttaki aşağı oklu " +
            "yuvarlak düğmeye basın. Uygulama ekranda görünenin ne kadar yer kaplayacağını gösterir: «Bu " +
            "alanı indir» ad sorar ve indirmeyi başlatır, «İptal» haritayı geri verir.",
    HelpKey.PreparationRegions to
        "İndirilen alanlar altta bir şerit hâlinde durur. Bir levhaya dokunmak haritada o alana uçar; " +
            "levhadaki düğmeler indirmeyi duraklatır ve sürdürür, hatadan sonra yeniden dener ve alanı " +
            "siler.",
    HelpKey.PreparationAreaSize to
        "Tam olarak ekranda görünen indirilir, bu yüzden haritayı kaydırdıkça boyut tahmini değişir. Alan " +
            "büyüdükçe daha az ayrıntılı olmak zorundadır — birkaç küçük alanı indirmek, tek bir devasa " +
            "alandan daha kârlıdır. Alan adları yinelenmemelidir.",
    HelpKey.PreparationBackground to
        "İndirme arka planda sürer ve ekrandan çıksanız da kesilmez; duraklatıldığında ilerleme korunur. " +
            "«Ayarlar»daki «Harita verilerini güncelle», kayıtlı tüm alanları baştan indirir.",
    HelpKey.DataPurpose to
        "Yürüyüşleri telefonlar arasında taşıma ve yedek: seçilen yürüyüşler tek bir arşiv dosyasına " +
            "yazılır, böyle bir dosya da geri yüklenebilir — bu cihazda ya da başka birinde.",
    HelpKey.DataExport to
        "Üstteki anahtar «Dışa aktar» ya da «İçe aktar» seçer. «Dışa aktar»da arşive bir ad verin, " +
            "yürüyüş seçme satırına dokunup istediklerinizi işaretleyin, sonra «Tamam» — telefon dosyanın " +
            "nereye kaydedileceğini soracak.",
    HelpKey.DataImport to
        "«İçe aktar»da «Dosya seç» düğmesine basın, isterseniz yüklenen yürüyüşlerin adlarına eklenecek " +
            "bir ek yazın ve «Tamam» düğmesine basın; arşiv okunduğunda «Arşive git» düğmesi belirir. " +
            "«İptal» düğmesi girilenleri hiçbir şey kaydetmeden temizler.",
    HelpKey.DataArchiveContents to
        "Arşive iz, bulgular, işaretlenmiş yerler, fotoğraflar ve katalogda bulunmayan mantar türleri " +
            "girer — diğer cihazda bunlar «Eklenen mantarlar» içinde «arşivden» etiketiyle görünür.",
    HelpKey.DataDuplicates to
        "İçe aktarma yürüyüşleri her zaman var olanların yanına ekler ve hiçbir şeyi değiştirmez, bu " +
            "yüzden aynı dosyayı yeniden yüklemek onları bir kez daha oluşturur: adlardaki ek, sonradan " +
            "ayırt etmeye yarar. Bitişte kaç yürüyüşün yüklendiği ve kaçının okunamadığı gösterilir.",
    HelpKey.SettingsPurpose to
        "Uygulamanın genel ayarları: arayüz dili, görünüm, kayıt ekranındaki mantar kutucuklarının biçimi " +
            "ve sırası, bir de harita bakımı.",
    HelpKey.SettingsLanguage to
        "«Arayüz dili» satırı dil listesini açar: dokunmak dili seçer, üstteki onay işareti seçimi " +
            "onaylar, ok hiçbir şey değiştirmeden çıkar. Dil tüm uygulamada hemen geçerli olur, yeniden " +
            "başlatmak gerekmez.",
    HelpKey.SettingsTheme to
        "«Görünüm» uygulamanın açık ve koyu temasını değiştirir. «Sistem» seçimi telefona bırakır: " +
            "uygulama onunla birlikte kararır ve aydınlanır.",
    HelpKey.SettingsMushroomSize to
        "Kaydırıcı, haritadaki mantar simgelerinin boyutunu belirler — hem kayıt ekranında hem de toplu " +
            "«Bulgu Haritası»nda. Altındaki görsel siz sürüklerken değişir, böylece boyutu bırakmadan " +
            "önce görürsünüz.",
    HelpKey.SettingsMushroomOrder to
        "Genelde yeni işaretlenen mantarlar kutucuk şeridinin başına çıkar. «Mantar sıralamasını sabitle» " +
            "bunu tümüyle kapatır, «Yürüyüş sonunda mantar sıralamasını sıfırla» ise yürüyüş bittiğinde " +
            "ilk sıralamayı geri getirir.",
    HelpKey.SettingsMapData to
        "«Harita verilerini güncelle» haritanın sunucuda değişip değişmediğine bakar ve değiştiyse " +
            "kayıtlı tüm çevrimdışı alanları yeniden indirir. «Harita önbelleğini temizle» yalnızca " +
            "gezinirken yüklenenleri siler — «Ön İndirme»deki alanlar yerinde kalır.",
)
