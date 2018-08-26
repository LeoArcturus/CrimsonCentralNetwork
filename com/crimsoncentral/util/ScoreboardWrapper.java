package com.crimsoncentral.util;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardWrapper {

	public static HashMap<Player, Scoreboard> boards = new HashMap<Player, Scoreboard>();
	private Scoreboard scoreboard;
	private int lastPage = 0;
	private final UUID uuid;

	/**
	 * Get the {@link Player} based on the {@link UUID} stored
	 *
	 * @return The player object, null if the player isn't online
	 */
	private Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	/**
	 * The UUID that was passed in the constructor
	 *
	 * @return the {@link UUID} for this instance
	 */
	public UUID getUniqueId() {
		return uuid;
	}

	/**
	 * The most recent page that was passed to the change page method.
	 *
	 * Can be used to increment or decrement pages on a command
	 *
	 * @return the most recent page set by {@link #changePage(int)}
	 **/
	public int getLastPage() {
		return lastPage;
	}

	/**
	 * Check if a page exists, returning it if it does, creating it if it doesn't
	 *
	 * @return the {@link Objective} associated with the page
	 * @param page
	 *                       the index number of the page to get
	 * @throws IllegalArgumentException
	 *                         if the page number is not between 0 and 15
	 *
	 **/
	private Objective getPage(int page) {
		// There's not a chatcolor for "10" the hex string for the number 16
		if (page > 15)
			throw new IllegalArgumentException("Page number must be between 0 and 15");

		// Checks if the object exists
		Objective obj = scoreboard.getObjective("page" + page);
		if (obj == null) {
			// Creates it if it doesn't
			obj = scoreboard.registerNewObjective("page" + page, "dummy");

			// Creates the 15 teams linked to the page, for example, team 0 on
			// page 0 would be ChatColor.BLACK + ChatColor.BLACK (§0§0)
			for (int i = 0; i < 15; i++) {
				scoreboard.registerNewTeam(ChatColor.getByChar(Integer.toHexString(page))
						+ ChatColor.getByChar(Integer.toHexString(i)).toString());
			}
		}
		return obj;
	}

	/**
	 * Getter for the Scoreboard field
	 *
	 * @return {@link Scoreboard} associated with this instance
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	/**
	 *
	 *
	 * @param uuid
	 *                       {@link UUID} of the player associated
	 */
	public ScoreboardWrapper(UUID uuid) {
		// Creates a new Scoreboard, getting the main one would cause
		// conflicting Scoreboards between players
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

		// So we know who to link the Scoreboard to
		this.uuid = uuid;

		// Creating page 0
		Objective obj = getPage(0);

		// Setting page 0 to show when the player's Scoreboard is set
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Set the title text for a specific page
	 *
	 * @param page
	 *                       Which page to change the title on
	 * @param title
	 *                       The title you want to be displayed
	 *
	 * @throws IllegalArgumentException
	 *                         if the page is not between 0 and 15
	 **/
	public void setTitle(int page, String title) {
		// Workaround for IllegalArgumentException if you pass null
		if (title == null)
			title = "";

		// Stops the IllegalArguementException if a string longer than 32
		// characters is passed
		if (title.length() > 32)
			title = title.substring(0, 32);

		// Gets the page and sets the title, unless the page is currently shown
		// the difference will not be noticeable to the client
		getPage(page).setDisplayName(title);
	}

	/**
	 * If the player's Scoreboard isn't the correct scoreboard it will be set If the
	 * Scoreboard isn't displayed the last page is shown If the Scoreboard is
	 * displayed it will be cleared
	 */
	public void toggleScoreboard() {
		// If the player's scoreboard isn't the current one then set it
		if (getPlayer() != null && !getPlayer().getScoreboard().equals(scoreboard))
			getPlayer().setScoreboard(scoreboard);

		// If the scoreboard doesn't have a sidebar then set it
		else if (scoreboard.getObjective(DisplaySlot.SIDEBAR) == null)
			getPage(lastPage).setDisplaySlot(DisplaySlot.SIDEBAR);

		// If the scoreboard does have a sidebar clear it
		else
			scoreboard.clearSlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Change the current displayed page to the page parameter If the Scoreboard
	 * isn't currently showing it will not force update
	 *
	 * @param page
	 *                       The page that you want to be changed to
	 */
	public void changePage(int page) {
		// To know what page the player is currently on, so they can get back to
		// it after toggling
		lastPage = page;

		// If the Scoreboard is currently toggled it will update, otherwise
		// it'll wait for the next toggle
		if (scoreboard.getObjective(DisplaySlot.SIDEBAR) != null)
			getPage(page).setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	/**
	 * Change the text displayed on a line
	 *
	 * @param page
	 *                       Which page you want to change the line on
	 * @param index
	 *                       Which line you want to change          
	 * @throws IllegalArgumentException
	 *                         if the line index is not between 0 and 14
	 * @throws IllegalArgumentException
	 *                         if the page index is not between 0 and 15
	 */
	public void setLineBlank(int page, int index) {
		setLine(page, index, "", "", false);
	}

	/**
	 * Change the text displayed on a line
	 *
	 * @param page
	 *                       Which page you want to change the line on
	 * @param index
	 *                       Which line you want to change
	 * @param string
	 *                       The String you want to be displayed. Characters past
	 *            index 32            will be ignored          
	 * @throws IllegalArgumentException
	 *                         if the line index is not between 0 and 14
	 * @throws IllegalArgumentException
	 *                         if the page index is not between 0 and 15
	 */
	public void setLine(int page, int index, String string) {
		setLine(page, index, string, true);
	}

	/**
	 *     * Change the text displayed on a line     *     * @param page     *      
	 *      Which page you want to change the line on     * @param index     *      
	 *      Which line you want to change     * @param prefix     *            The
	 * String you want to be displayed in the prefix slot. Characters past index 16
	 *     *            will be ignored     *               * @param suffix     *  
	 *          The String you want to be displayed in the suffix slot. Characters
	 * past index 16     *            will be ignored     *               * @throws
	 * IllegalArgumentException     *             if the line index is not between 0
	 * and 14     * @throws IllegalArgumentException     *             if the page
	 * index is not between 0 and 15
	 */
	public void setLine(int page, int index, String prefix, String suffix) {
		setLine(page, index, prefix, suffix, true);
	}

	/**
	 *     * Change the text displayed on a line     *     * @param page     *      
	 *      Which page you want to change the line on     * @param index     *      
	 *      Which line you want to change     * @param string     *            The
	 * String you want to be displayed. Characters past index 32     *          
	 *  will be ignored     * @param copyPreviousColors     *              If your
	 * string accidently goes past index 16, use this to ensure     *     * @throws
	 * IllegalArgumentException     *             if the line index is not between 0
	 * and 14     * @throws IllegalArgumentException     *             if the page
	 * index is not between 0 and 15    
	 */
	public void setLine(int page, int index, String string, boolean copyPreviousColors) {
		// Check to see if it'll need both the prefix and suffix
		if (string.length() > 16)
			setLine(page, index, string.substring(0, 16), string.substring(16), copyPreviousColors);
		else
			setLine(page, index, string, "", copyPreviousColors);
	}

	/**
	 *     * Change the text displayed on a line     *     * @param page     *      
	 *      Which page you want to change the line on     * @param index     *      
	 *      Which line you want to change     * @param prefix     *            The
	 * String you want to be displayed in the prefix slot. Characters past index 16
	 *     *            will be ignored     * @param suffix     *            The
	 * String you want to be displayed in the suffix slot. Characters past index 16
	 *     *            will be ignored     * @param copyPreviousColors     *      
	 *        If your string accidently goes past index 16, use this to ensure     *
	 *               * @throws IllegalArgumentException     *             if the
	 * line index is not between 0 and 14     * @throws IllegalArgumentException    
	 * *             if the page index is not between 0 and 15    
	 */
	public void setLine(int page, int index, String prefix, String suffix, boolean copyPreviousColors) {
		if (prefix.length() > 16)
			prefix = prefix.substring(0, 16);
		if (suffix.length() > 16)
			suffix = suffix.substring(0, 16);
		if (index < 0 || index > 14)
			throw new IllegalArgumentException("You can only get a line from 0 - 14");

		// Getting the objective, creating it if it doesn't exist
		Objective obj = getPage(page);

		// Getting what line was set, since the number is less than 16 there
		// will always be a chat color associated with the number to hex
		String name = ChatColor.getByChar(Integer.toHexString(page))
				+ ChatColor.getByChar(Integer.toHexString(index)).toString();

		// Adding the reset means all characters in the suffix won't have the
		// team color, but instead will have the default white text
		Score score = obj.getScore(name + ChatColor.RESET);
		Team team = scoreboard.getTeam(name);

		// Setting the score if it isn't already
		if (!score.isScoreSet()) {
			score.setScore(index);
			team.addEntry(score.getEntry());
		}

		team.setPrefix(prefix);

		if (copyPreviousColors) {
			// Apply the previous color
			suffix = ChatColor.getLastColors(prefix) + suffix;
			if (suffix.length() > 16)
				// Makes sure it's still within the valid length.
				suffix = suffix.substring(0, 16);
		}
		team.setSuffix(suffix);
	}
}