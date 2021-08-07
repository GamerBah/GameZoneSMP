package com.gamerbah.gamezonesmp.data;
/* Created by GamerBah on 8/8/2016 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static com.gamerbah.gamezonesmp.data.Punishment.Type.*;

@Data
@AllArgsConstructor
public class Punishment {

	private       int           id;
	private final Type          type;
	private final LocalDateTime date;
	private final int           duration;
	private final LocalDateTime expiration;
	private final int           enforcer;
	private final Reason        reason;
	private       boolean       pardoned;


	public static Type getTypeFromId(int id) {
		return Type.fromId(id);
	}

	public static Reason getReasonFromId(int id) {
		return Reason.fromId(id);
	}

	@Override
	public int hashCode() {
		int code = 0;
		code += id;
		code += date.hashCode();
		code += duration;
		code += enforcer;
		code += expiration.hashCode();
		code += (pardoned ? 1 : 0);
		code += reason.toString().hashCode();
		code += type.toString().hashCode();
		return code;
	}

	@AllArgsConstructor
	@Getter
	public enum Type {
		BAN(1, "Ban"),
		TEMP_BAN(2, "Temp-Ban"),
		MUTE(3, "Mute"),
		KICK(4, "Kick"),
		AUTO(5, "Auto"),
		KICK_BAN(6, null),
		ALL(7, null);

		private final int    id;
		private final String name;

		public static Type fromId(int id) {
			Optional<Type> optional = Arrays.stream(Type.values()).filter(r -> r.id == id).findFirst();
			return optional.orElse(ALL);
		}

	}

	@AllArgsConstructor
	@Getter
	public enum Reason {
		SPAM_CAPS(1,
		          MUTE,
		          "Spam (Caps)",
		          "Player is spamming the chat,with fully capitalized words",
		          "Please don't spam the chat with capitalized words!",
		          1500),
		SPAM_LETTERS(2,
		             MUTE,
		             "Spam (Letters)",
		             "Player is spamming the,chat with random letters",
		             "Please don't spam the chat with random letters!",
		             900),
		SPAM_GENERIC(3,
		             MUTE,
		             "Spam (Generic)",
		             "Player is spamming the,chat in some sort of way",
		             "Please don't spam the chat! We want to keep it clean!",
		             1200),
		SWEARING(4,
		         ALL,
		         "Swearing",
		         "Player is using profane words in,either public chat or private messages",
		         "Please don't swear! There are kids that play on the server!",
		         1500),
		ATTEMPT_SWEARING(5,
		                 AUTO,
		                 "Attempted Swearing",
		                 "Player is attempting to swear,in the chat but is being blocked",
		                 "Attempting to swear can get you in trouble too!",
		                 500),
		HARASSMENT(6,
		           ALL,
		           "Player Harassment",
		           "Player is verbally harassing others,and creating a toxic environment",
		           "Harassment of other players is not tolerated.",
		           1800),
		GLITCH_ABUSE(7,
		             KICK_BAN,
		             "Glitch Exploiting",
		             "Player was caught exploiting,a glitch with the plugin or arena",
		             "If you find a bug, please report it! Don't use it to your advantage!",
		             1500),
		DISRESPECT(8,
		           ALL,
		           "Player Disrespect",
		           "Player was disrespecting a,Staff member or other player",
		           "Please treat the Staff and our players with respect.",
		           1800),
		MODDED_CLIENT(9,
		              BAN,
		              "Modded Client",
		              "Player was found using a modded,client to gain an advantage",
		              "Hacked Clients are not allowed. Please read our rules before joining.",
		              -1),
		MODDED_CLIENT_SUSPECTED(10,
		                        BAN,
		                        "Suspected Modded Client",
		                        "Player was suspected of,using a modded client",
		                        "You were suspected of having a hacked client!",
		                        3600),
		OTHER(11, ALL, "Other", "This is not,a valid reason", "If you see this, contact an administrator!", 0);

		private final int    id;
		private final Type   type;
		private final String name;
		private final String description;
		private final String message;
		private final int    length;

		public static Reason fromId(int id) {
			Optional<Reason> optional = Arrays.stream(Reason.values()).filter(r -> r.id == id).findFirst();
			return optional.orElse(OTHER);
		}

	}

}
