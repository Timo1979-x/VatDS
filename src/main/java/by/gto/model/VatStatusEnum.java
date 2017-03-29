package by.gto.model;

public enum VatStatusEnum {

    UNKNOWN("неизвестно", "неизвестно"), //0
    COMPLETED("Выставлена", "Выставлена"), // 1
    COMPLETED_SIGNED("Подписана", "Выставлена. Подписана получателем"), //2
    ON_AGREEMENT("На согласовании", "На согласовании"), //3
    CANCELLED("Аннулирована", "Аннулирована"), //4
    ON_AGREEMENT_CANCEL("Аннулирована поставщиком", "Выставлена. Аннулирована поставщиком"), //5
    IN_PROGRESS("В обработке", "ЭСЧФ находится в обработке. Запросите статус повторно через 3 часа"), // 6
    NOT_FOUND("Не найдена", "ЭСЧФ нет в базе или нет права для просмотра статуса/выгрузки документа"), // 7
    ERROR("Ошибка", "Ошибка при выставлении ЭСЧФ на портал"), // 8
    DENIED("DENIED", "DENIED"); // 9

    private String name, description;

    VatStatusEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }

    public static VatStatusEnum getByOrdinal(int ordinal) {
        try {
            return  VatStatusEnum.values()[ordinal];
        } catch (Exception ignored) {
            return UNKNOWN;
        }
    }
}

