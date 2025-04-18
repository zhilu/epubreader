package com.ereader.view;

import com.ereader.model.ReadingMode;
import com.ereader.util.DesktopUtil;
import com.ereader.service.OpenAIService;
import nl.siegmann.epublib.Constants;
import nl.siegmann.epublib.browsersupport.NavigationEvent;
import nl.siegmann.epublib.browsersupport.NavigationEventListener;
import nl.siegmann.epublib.browsersupport.Navigator;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Displays a page
 * 
 */
public class ReadingContentPanel extends JPanel implements NavigationEventListener, HyperlinkListener {

	private static final long serialVersionUID = -5322988066178102320L;

	private static final Logger log = LoggerFactory.getLogger(ReadingContentPanel.class);


	private Navigator navigator;
	private Resource currentResource;
	private JEditorPane editorPane;
	private JScrollPane scrollPane;
	private JSplitPane splitPane;
	private JPanel translationPanel;
	private JTextPane translationPane;
	private boolean isTranslationPanelVisible = false;

	private HTMLDocumentFactory htmlDocumentFactory;
	private ReadingMode readingMode = ReadingMode.WHITE;

	public void setReadingMode(ReadingMode readingMode){
		if(Objects.equals(readingMode,this.readingMode)){
			return;
		}
		this.readingMode = readingMode;
	}

	public ReadingContentPanel(Navigator navigator) {
		super(new GridLayout(1,0));
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setDividerLocation(400);
		splitPane.setResizeWeight(0.70);

		add(splitPane);

		this.scrollPane = new JScrollPane();
		this.scrollPane.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {

			}
			
			@Override
			public void keyReleased(KeyEvent e) {

			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					int newY = (int) (viewPosition.getY() + 10);
					scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
				}
			}
		});
		this.scrollPane.addMouseWheelListener(new MouseWheelListener() {
			
			private boolean gotoNextPage = false;
			private boolean gotoPreviousPage = false;

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
			    int notches = e.getWheelRotation();
			    int increment = scrollPane.getVerticalScrollBar().getUnitIncrement(1);
			    if (notches < 0) {
					Point viewPosition = scrollPane.getViewport().getViewPosition();
					if (viewPosition.getY() - increment < 0) {
						if (gotoPreviousPage) {
							gotoPreviousPage = false;
							navigator.gotoPreviousSpineSection(-1, this);
						} else {
							gotoPreviousPage = true;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), 0));
						}
					}
			    } else {
			    	// only move to the next page if we are exactly at the bottom of the current page
			    	Point viewPosition = scrollPane.getViewport().getViewPosition();
					int viewportHeight = scrollPane.getViewport().getHeight();
					int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
					if (viewPosition.getY() + viewportHeight + increment > scrollMax) {
						if (gotoNextPage) {
							gotoNextPage = false;
							ReadingContentPanel.this.navigator.gotoNextSpineSection(this);
						} else {
							gotoNextPage = true;
							int newY = scrollMax - viewportHeight;
							scrollPane.getViewport().setViewPosition(new Point((int) viewPosition.getX(), newY));
						}
					}
			    }
			  }
		});

		translationPane = new JTextPane();
		translationPane.setContentType("text/html");
		translationPane.setEditable(false);

		translationPanel = new JPanel(new BorderLayout());
		translationPanel.add(new JScrollPane(translationPane), BorderLayout.CENTER);
		translationPanel.setPreferredSize(new Dimension(300, getHeight()));

		this.navigator = navigator;
		navigator.addNavigationEventListener(this);
		this.editorPane = createJEditorPane();
		scrollPane.getViewport().add(editorPane);
		splitPane.add(scrollPane);
		this.htmlDocumentFactory = new HTMLDocumentFactory(navigator, editorPane.getEditorKit());
		initBook(navigator.getBook());
	}

	private void initBook(Book book) {
		if (book == null) {
			return;
		}
		htmlDocumentFactory.init(book);
		displayPage(book.getCoverPage());
	}
	
	
	
	/**
	 * Whether the given searchString matches any of the possibleValues.
	 * 
	 * @param searchString
	 * @param possibleValues
	 * @return Whether the given searchString matches any of the possibleValues.
	 */
	private static boolean matchesAny(String searchString, String... possibleValues) {
		for (int i = 0; i < possibleValues.length; i++) {
			String attributeValue = possibleValues[i];
			if (StringUtils.isNotBlank(attributeValue) && (attributeValue.equals(searchString))) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * Scrolls the editorPane to the startOffset of the current element in the elementIterator
	 * 
	 * @param editorPane
	 * @param elementIterator
	 * 
	 * @return whether it was a match and we jumped there.
	 */
	private static void scrollToElement(JEditorPane editorPane, HTMLDocument.Iterator elementIterator) {
		try {
			Rectangle rectangle = editorPane.modelToView(elementIterator.getStartOffset());
			if (rectangle == null) {
				return;
			}
			// the view is visible, scroll it to the
			// center of the current visible area.
			Rectangle visibleRectangle = editorPane.getVisibleRect();
			// r.y -= (vis.height / 2);
			rectangle.height = visibleRectangle.height;
			editorPane.scrollRectToVisible(rectangle);
		} catch (BadLocationException e) {
			log.error(e.getMessage());
		}
	}
	
	
	/**
	 * Scrolls the editorPane to the first anchor element whose id or name matches the given fragmentId.
	 * 
	 * @param fragmentId
	 */
	private void scrollToNamedAnchor(String fragmentId) {
		HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
		for (HTMLDocument.Iterator iter = doc.getIterator(HTML.Tag.A); iter.isValid(); iter.next()) {
			AttributeSet attributes = iter.getAttributes();
			if (matchesAny(fragmentId, (String) attributes.getAttribute(HTML.Attribute.NAME),
					(String) attributes.getAttribute(HTML.Attribute.ID))) {
				scrollToElement(editorPane, iter);
				break;
			}
		}
	}

	private JEditorPane createJEditorPane() {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setBackground(Color.white);
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);


		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) { // 右键点击
					showPopupMenu(e, editorPane);
				}
			}
		});


		HTMLEditorKit htmlKit = new HTMLEditorKit();
		// StyleSheet myStyleSheet = new StyleSheet();
		// String normalTextStyle = "font-size: 12px, font-family: georgia";
		// myStyleSheet.addRule("body {" + normalTextStyle + "}");
		// myStyleSheet.addRule("p {" + normalTextStyle + "}");
		// myStyleSheet.addRule("div {" + normalTextStyle + "}");
		// htmlKit.setStyleSheet(myStyleSheet);



		htmlKit.getStyleSheet().addRule("body { padding: 20px; }");
		htmlKit.getStyleSheet().addRule("body { background-color: #FAF3E0; color: #333333;}");
		htmlKit.getStyleSheet().addRule("p { font-size: 18px; font-family: serif; word-wrap: break-word; white-space: normal; margin-bottom: 20px; }");
		htmlKit.getStyleSheet().addRule("h1 { font-size: 28px; font-family: serif; color: #333333; }");
		htmlKit.getStyleSheet().addRule("h2 { font-size: 26px; font-family: serif; }");
		htmlKit.getStyleSheet().addRule("h3 { font-size: 24px; font-family: serif; }");
		htmlKit.getStyleSheet().addRule("h4 { font-size: 24px; font-family: serif; }");
		htmlKit.getStyleSheet().addRule("h5 { font-size: 24px; font-family: serif; }");
		htmlKit.getStyleSheet().addRule("h6 { font-size: 20px; font-family: serif; }");
		htmlKit.getStyleSheet().addRule("figure { margin-top: 40px; margin-bottom: 40px; }");
		htmlKit.getStyleSheet().addRule("img { display: block; margin-left: auto; margin-right: auto; margin-bottom: 20px; }");

		htmlKit.getStyleSheet().addRule("pre { background-color: #f4f4f4; border: 1px solid #ddd; padding: 10px; overflow-x: auto; }");
		htmlKit.getStyleSheet().addRule("code { font-family: 'Courier New', Courier, monospace; font-size: 14px; color: #d6336c; }");


		editorPane.setEditorKit(htmlKit);
		editorPane.addHyperlinkListener(this);
		editorPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent keyEvent) {
			}

			@Override
			public void keyReleased(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
					navigator.gotoNextSpineSection(ReadingContentPanel.this);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
					navigator.gotoPreviousSpineSection(ReadingContentPanel.this);
//				} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
//					ContentPane.this.gotoPreviousPage();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
//					|| (keyEvent.getKeyCode() == KeyEvent.VK_DOWN)) {
					ReadingContentPanel.this.gotoNextPage();
				}
			}
		});
		return editorPane;
	}

	private void showPopupMenu(MouseEvent e, JEditorPane editorPane) {
		JPopupMenu popupMenu = new JPopupMenu();

		String selectedText = editorPane.getSelectedText();

		JMenuItem translateItem = new JMenuItem("翻译");
		translateItem.addActionListener(ev -> translateText(selectedText));
		popupMenu.add(translateItem);

		// 添加 "标记文本" 选项
		JMenuItem highlightItem = new JMenuItem("标记文本");
		highlightItem.addActionListener(ev -> highlightText(editorPane));
		popupMenu.add(highlightItem);

		// 显示菜单
		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	private void highlightText(JEditorPane editorPane) {
		String selectedText = editorPane.getSelectedText();
		if (selectedText == null || selectedText.isEmpty()) {
			return;
		}
		int start = editorPane.getSelectionStart();
		int end = editorPane.getSelectionEnd();

		Highlighter highlighter = editorPane.getHighlighter();
		Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
		try {
			highlighter.addHighlight(start, end, painter);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
	}

	private StringBuilder htmlBuilder = new StringBuilder("<html><body style='font-family:sans-serif;'>");


	private void translateText(String text) {
		if (text == null || text.isEmpty()) return;

		String translatedText = OpenAIService.INSTANCE.translate(text);

		// 动态添加右侧翻译栏（如果尚未添加）
		if (!isTranslationPanelVisible) {
			splitPane.add(translationPane);
			this.revalidate(); // 通知重新布局
			this.repaint();
			isTranslationPanelVisible = true;
		}

		String originText = text;
		if(text.length()>50){
			originText = text.substring(0, 20) + "..." + text.substring(text.length() - 25);
		}

		// 美化的 HTML 显示内容
		htmlBuilder.append("<div style='margin-bottom:15px;'>")
				.append("<div style='color:#555;font-weight:bold;'>原文：</div>")
				.append("<div style='margin:5px 0;'>" + escapeHtml(originText) + "</div>")
				.append("<div style='color:#2a7ae2;font-weight:bold;'>翻译：</div>")
				.append("<div style='margin:5px 0;'>" + escapeHtml(translatedText) + "</div>")
				.append("<hr style='border:0;border-top:1px solid #ccc;'/>")
				.append("</div>");

		translationPane.setText(htmlBuilder.toString());
		translationPane.setCaretPosition(translationPane.getDocument().getLength());
	}

	private String escapeHtml(String text) {
		return text.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\n", "<br>");
	}

	public void displayPage(Resource resource) {
		displayPage(resource, 0);
	}

	public void displayPage(Resource resource, int sectionPos) {
		if (resource == null) {
			return;
		}
		try {
			HTMLDocument document = htmlDocumentFactory.getDocument(resource);
			if (document == null) {
				return;
			}
			currentResource = resource;
			log.debug(new String(resource.getData(), StandardCharsets.UTF_8));
			editorPane.setDocument(document);
			scrollToCurrentPosition(sectionPos);
		} catch (Exception e) {
			log.error("When reading resource " + resource.getId() + "("
					+ resource.getHref() + ") :" + e.getMessage(), e);
		}
	}

	private void scrollToCurrentPosition(int sectionPos) {
		if (sectionPos < 0) {
			editorPane.setCaretPosition(editorPane.getDocument().getLength());
		} else {
			editorPane.setCaretPosition(sectionPos);
		}
		if (sectionPos == 0) {
			scrollPane.getViewport().setViewPosition(new Point(0, 0));
		} else if (sectionPos < 0) {
			int viewportHeight = scrollPane.getViewport().getHeight();
			int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
			scrollPane.getViewport().setViewPosition(new Point(0, scrollMax - viewportHeight));
		}
	}

	public void hyperlinkUpdate(HyperlinkEvent event) {
		if (event.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
			return;
		}
        final URL url = event.getURL();
        if (url.getProtocol().toLowerCase().startsWith("http") && !"".equals(url.getHost())) {
            try {
                DesktopUtil.launchBrowser(event.getURL());
                return;
            } catch (DesktopUtil.BrowserLaunchException ex) {
                log.warn("Couldn't launch system web browser.", ex);
            }
        }
		String resourceHref = calculateTargetHref(event.getURL());
		if (resourceHref.startsWith("#")) {
			scrollToNamedAnchor(resourceHref.substring(1));
			return;
		}

		Resource resource = navigator.getBook().getResources().getByHref(resourceHref);
		if (resource == null) {
			log.error("Resource with url " + resourceHref + " not found");
		} else {
			navigator.gotoResource(resource, this);
		}
	}

	public void gotoPreviousPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		if (viewPosition.getY() <= 0) {
			navigator.gotoPreviousSpineSection(this);
			return;
		}
		int viewportHeight = scrollPane.getViewport().getHeight();
		int newY = (int) viewPosition.getY();
		newY -= viewportHeight;
		newY = Math.max(0, newY - viewportHeight);
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
	}

	public void gotoNextPage() {
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		int viewportHeight = scrollPane.getViewport().getHeight();
		int scrollMax = scrollPane.getVerticalScrollBar().getMaximum();
		if (viewPosition.getY() + viewportHeight >= scrollMax) {
			navigator.gotoNextSpineSection(this);
			return;
		}
		int newY = ((int) viewPosition.getY()) + viewportHeight;
		scrollPane.getViewport().setViewPosition(
				new Point((int) viewPosition.getX(), newY));
	}

	
	/**
	 * Transforms a link generated by a click on a link in a document to a resource href.
	 * Property handles http encoded spaces and such.
	 * 
	 * @param clickUrl
	 * @return a link generated by a click on a link transformed into a document to a resource href.
	 */
	private String calculateTargetHref(URL clickUrl) {
		String resourceHref = clickUrl.toString();
		try {
			resourceHref = URLDecoder.decode(resourceHref,
					Constants.CHARACTER_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
		}
		resourceHref = resourceHref.substring(ImageLoaderCache.IMAGE_URL_PREFIX
				.length());

		if (resourceHref.startsWith("#")) {
			return resourceHref;
		}
		if (currentResource != null
				&& StringUtils.isNotBlank(currentResource.getHref())) {
			int lastSlashPos = currentResource.getHref().lastIndexOf('/');
			if (lastSlashPos >= 0) {
				resourceHref = currentResource.getHref().substring(0,
						lastSlashPos + 1)
						+ resourceHref;
			}
		}
		return resourceHref;
	}

	
	public void navigationPerformed(NavigationEvent navigationEvent) {
		if (navigationEvent.isBookChanged()) {
			initBook(navigationEvent.getCurrentBook());
		} else {
			if (navigationEvent.isResourceChanged()) {
			displayPage(navigationEvent.getCurrentResource(),
					navigationEvent.getCurrentSectionPos());
			} else if (navigationEvent.isSectionPosChanged()) {
				editorPane.setCaretPosition(navigationEvent.getCurrentSectionPos());
			}
			if (StringUtils.isNotBlank(navigationEvent.getCurrentFragmentId())) {
				scrollToNamedAnchor(navigationEvent.getCurrentFragmentId());
			}
		}
	}


}
