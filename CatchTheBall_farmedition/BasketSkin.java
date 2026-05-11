import java.awt.*;

public enum BasketSkin {

    WOVEN("Woven Basket", 0) {
        @Override
        public void draw(Graphics2D g, int x, int y, int w, int h) {
            int[] bx = { x, x + w, x + w - 8, x + 8 };
            int[] by = { y, y, y + h, y + h };
            g.setColor(new Color(0xC8853A));
            g.fillPolygon(bx, by, 4);

            g.setColor(new Color(0x7A4A18));
            g.setStroke(new BasicStroke(1f));
            for (int row = 1; row <= 3; row++) {
                int lineY = y + h * row / 4;
                g.drawLine(x + 2, lineY, x + w - 2, lineY);
            }
            for (int col = 1; col <= 4; col++) {
                int lx = x + w * col / 5;
                g.drawLine(lx, y + 2, lx + 6, y + h - 2);
            }

            g.setStroke(new BasicStroke(1.5f));
            g.drawPolygon(bx, by, 4);

            g.setColor(new Color(0xA86A28));
            g.fillRoundRect(x - 3, y - 5, w + 6, 10, 6, 6);
            g.setColor(new Color(0x7A4A18));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x - 3, y - 5, w + 6, 10, 6, 6);

            g.setColor(new Color(0x8B5A1A));
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(x + w / 6, y - h / 2, w * 2 / 3, h / 2, 0, 180);
            g.setStroke(new BasicStroke(1f));
        }
    },

    METAL("Metal Bucket", 80) {
        @Override
        public void draw(Graphics2D g, int x, int y, int w, int h) {
            int[] bx = { x, x + w, x + w - 6, x + 6 };
            int[] by = { y, y, y + h, y + h };
            GradientPaint grad = new GradientPaint(x, 0, new Color(0x8899AA), x + w, 0, new Color(0xC8D4DC));
            g.setPaint(grad);
            g.fillPolygon(bx, by, 4);

            g.setPaint(null);
            g.setColor(new Color(0xD8E8F0));
            g.setStroke(new BasicStroke(1.5f));
            for (int i = 1; i <= 2; i++) {
                int lineY = y + h * i / 3;
                g.drawLine(x + 2, lineY, x + w - 2, lineY);
            }

            g.setColor(new Color(0x6A7A8A));
            g.setStroke(new BasicStroke(1.5f));
            g.drawPolygon(bx, by, 4);

            GradientPaint rimGrad = new GradientPaint(x, 0, new Color(0x7A8A9A), x + w, 0, new Color(0xDCECF4));
            g.setPaint(rimGrad);
            g.fillRoundRect(x - 3, y - 5, w + 6, 10, 5, 5);
            g.setPaint(null);
            g.setColor(new Color(0x5A6A7A));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x - 3, y - 5, w + 6, 10, 5, 5);

            g.setColor(new Color(0x8A9AAA));
            g.fillOval(x + 2, y - 2, 7, 7);
            g.fillOval(x + w - 9, y - 2, 7, 7);

            g.setColor(new Color(0x7A8A9A));
            g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(x + w / 5, y - h / 2, w * 3 / 5, h / 2, 0, 180);

            g.setColor(new Color(0xC8D8E4));
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(x + w / 5 + 2, y - h / 2 + 3, w * 3 / 5 - 4, h / 2 - 6, 20, 140);
            g.setStroke(new BasicStroke(1f));
        }
    },

    GOLDEN("Golden Basket", 200) {
        @Override
        public void draw(Graphics2D g, int x, int y, int w, int h) {
            int[] bx = { x, x + w, x + w - 8, x + 8 };
            int[] by = { y, y, y + h, y + h };
            GradientPaint gold = new GradientPaint(x, 0, new Color(0xB8860B), x + w / 2, 0, new Color(0xFFD700));
            g.setPaint(gold);
            g.fillPolygon(bx, by, 4);

            g.setPaint(null);
            g.setColor(new Color(0x8B6A00));
            g.setStroke(new BasicStroke(0.8f));
            for (int i = 1; i <= 3; i++) {
                int lineY = y + h * i / 4;
                g.drawLine(x + 2, lineY, x + w - 2, lineY);
            }

            int cx = x + w / 2;
            int cy = y + h / 2;
            int[] dx = { cx, cx + 5, cx, cx - 5 };
            int[] dy = { cy - 7, cy, cy + 7, cy };
            g.setColor(new Color(0xFFE840));
            g.fillPolygon(dx, dy, 4);
            g.setColor(new Color(0xC8960C));
            g.setStroke(new BasicStroke(0.8f));
            g.drawPolygon(dx, dy, 4);

            g.setColor(new Color(0xB8860B));
            g.setStroke(new BasicStroke(1.5f));
            g.drawPolygon(bx, by, 4);

            GradientPaint rimGold = new GradientPaint(x, 0, new Color(0xC8960C), x + w / 2, 0, new Color(0xFFE840));
            g.setPaint(rimGold);
            g.fillRoundRect(x - 3, y - 5, w + 6, 10, 6, 6);
            g.setPaint(null);
            g.setColor(new Color(0xB8860B));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x - 3, y - 5, w + 6, 10, 6, 6);

            g.setColor(new Color(0xC8960C));
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(x + w / 6, y - h / 2, w * 2 / 3, h / 2, 0, 180);
            g.setColor(new Color(0xFFE840));
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(x + w / 6 + 3, y - h / 2 + 4, w * 2 / 3 - 6, h / 2 - 8, 10, 160);

            g.setColor(new Color(0xFFFFCC));
            g.setStroke(new BasicStroke(1f));
            g.fillOval(x + w - 14, y - 10, 4, 4);
            g.fillOval(x + 10, y - 8, 3, 3);
            g.fillOval(cx - 1, y - 14, 3, 3);
            g.setStroke(new BasicStroke(1f));
        }
    },

    CART("Farm Cart", 300) {
        @Override
        public void draw(Graphics2D g, int x, int y, int w, int h) {
            int boxH = h - 14;
            int wheelR = 12;

            GradientPaint wood = new GradientPaint(x, y, new Color(0xC8883A), x, y + boxH, new Color(0x8B5A1A));
            g.setPaint(wood);
            g.fillRoundRect(x, y, w, boxH, 4, 4);

            g.setPaint(null);
            g.setColor(new Color(0x6B3A10));
            g.setStroke(new BasicStroke(0.8f));
            for (int i = 1; i <= 2; i++) {
                g.drawLine(x, y + boxH * i / 3, x + w, y + boxH * i / 3);
            }
            for (int i = 1; i <= 3; i++) {
                g.drawLine(x + w * i / 4, y, x + w * i / 4, y + boxH);
            }
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(x, y, w, boxH, 4, 4);

            g.setColor(new Color(0x888888));
            g.fillRoundRect(x - 1, y - 3, w + 2, 6, 3, 3);
            g.setColor(new Color(0x555555));
            g.setStroke(new BasicStroke(0.8f));
            g.drawRoundRect(x - 1, y - 3, w + 2, 6, 3, 3);

            int wy = y + boxH + wheelR - 2;
            int[] wheelXArr = { x + wheelR + 4, x + w - wheelR - 4 };
            for (int wx : wheelXArr) {
                g.setColor(new Color(0x3A2208));
                g.setStroke(new BasicStroke(4f));
                g.drawOval(wx - wheelR, wy - wheelR, wheelR * 2, wheelR * 2);

                g.setColor(new Color(0x5A3A10));
                g.setStroke(new BasicStroke(1.5f));
                g.drawLine(wx, wy - wheelR, wx, wy + wheelR);
                g.drawLine(wx - wheelR, wy, wx + wheelR, wy);
                g.drawLine(wx - 8, wy - 8, wx + 8, wy + 8);
                g.drawLine(wx + 8, wy - 8, wx - 8, wy + 8);

                g.setColor(new Color(0x6B3A10));
                g.fillOval(wx - 4, wy - 4, 8, 8);
                g.setColor(new Color(0x3A2208));
                g.setStroke(new BasicStroke(1f));
                g.drawOval(wx - 4, wy - 4, 8, 8);
            }

            g.setColor(new Color(0x6B3A10));
            g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + w - 2, y + boxH / 2, x + w + 16, y + boxH / 4);
            g.drawLine(x + w + 16, y + boxH / 4, x + w + 16, y - 6);
            g.setStroke(new BasicStroke(1f));
        }
    },

    DIAMOND("Diamond Basket", 500) {
        @Override
        public void draw(Graphics2D g, int x, int y, int w, int h) {
            int cx = x + w / 2;

            int[] px = { cx - w / 2, cx, cx + w / 2, cx + w / 2 - 6, cx, cx - w / 2 + 6 };
            int[] py = { y + h / 4,  y,  y + h / 4,  y + h,          y + h, y + h };
            GradientPaint iceGrad = new GradientPaint(x, y, new Color(0xC8EEFF), x + w, y + h, new Color(0x60A8D0));
            g.setPaint(iceGrad);
            g.fillPolygon(px, py, 6);

            g.setPaint(null);
            int[] ux = { cx - w / 2, cx, cx + w / 2, cx };
            int[] uy = { y + h / 4,  y,  y + h / 4,  y + h / 2 };
            g.setColor(new Color(220, 242, 255, 180));
            g.fillPolygon(ux, uy, 4);

            g.setColor(new Color(0x48A0D8));
            g.setStroke(new BasicStroke(0.8f));
            g.drawLine(cx, y, cx, y + h);
            g.drawLine(cx - w / 2, y + h / 4, cx + w / 2 - 6, y + h);
            g.drawLine(cx + w / 2, y + h / 4, cx - w / 2 + 6, y + h);
            g.drawLine(cx - w / 2, y + h / 4, cx + w / 2, y + h / 4);

            g.setColor(new Color(0x3880B8));
            g.setStroke(new BasicStroke(1.5f));
            g.drawPolygon(px, py, 6);

            GradientPaint rimGrad = new GradientPaint(x, 0, new Color(0x5898C8), x + w, 0, new Color(0xC8ECFF));
            g.setPaint(rimGrad);
            g.fillRoundRect(x - 3, y - 5, w + 6, 10, 5, 5);
            g.setPaint(null);
            g.setColor(new Color(0x48A0D8));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x - 3, y - 5, w + 6, 10, 5, 5);

            g.setColor(new Color(0x78C0E8));
            g.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(cx - w / 4, y - h / 2, w / 2, h / 2, 0, 180);
            g.setColor(new Color(0xE0F6FF));
            g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc(cx - w / 4 + 3, y - h / 2 + 4, w / 2 - 6, h / 2 - 8, 20, 140);

            g.setColor(new Color(0xB0E0FF));
            g.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + w + 4, y + 6,  x + w + 10, y);
            g.drawLine(x + w + 6, y + 14, x + w + 14, y + 14);
            g.drawLine(x + w + 4, y + 22, x + w + 10, y + 28);
            g.setStroke(new BasicStroke(1f));
        }
    };

    private final String displayName;
    private final int cost;

    BasketSkin(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }

    public abstract void draw(Graphics2D g, int x, int y, int w, int h);
    public String getDisplayName() { return displayName; }
    public int getCost() { return cost; }
}