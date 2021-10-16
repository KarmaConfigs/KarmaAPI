package ml.karmaconfigs.api.common.utils.string;

import java.util.Random;

public final class RandomString {

    private final OptionsBuilder options;

    RandomString() {
        options = new OptionsBuilder();
    }

    RandomString(final OptionsBuilder opts) {
        options = opts;
    }

    public String create() {
        char[] salt;
        switch (options.getContent()) {
            case ONLY_NUMBERS:
                salt = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                break;
            case NUMBERS_AND_LETTERS:
                salt = new char[]{
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                        'u', 'v', 'x', 'y', 'z', '0', '1', '2', '3', '4',
                        '5', '6', '7', '8', '9'};
                break;
            default:
                salt = new char[]{
                        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                        'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                        'u', 'v', 'x', 'y', 'z'};
                break;
        }
        StringBuilder result = new StringBuilder();
        int last_int = 0;
        for (int i = 0; i < options.getSize(); i++) {
            int random = (new Random()).nextInt(salt.length);
            if (last_int != random) {
                int random_s;
                String lower = String.valueOf(salt[random]);
                String upper = String.valueOf(salt[random]).toUpperCase();
                switch (options.getType()) {
                    case ALL_LOWER:
                        result.append(lower);
                        break;
                    case ALL_UPPER:
                        result.append(upper);
                        break;
                    default:
                        random_s = (new Random()).nextInt(100);
                        if (random_s > 50) {
                            result.append(lower);
                            break;
                        }
                        result.append(upper);
                        break;
                }
                last_int = random;
            } else {
                i--;
            }
        }

        return result.toString();
    }

    public static OptionsBuilder createBuilder() {
        return new OptionsBuilder();
    }
}
