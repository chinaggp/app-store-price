-- 插入初始分类数据
INSERT OR IGNORE INTO category (id, name, description, icon, sort_order, created_at) VALUES 
('productivity', '效率', '笔记、任务、文档与自动化工具', '⚡', 1, datetime('now')),
('design', '设计', '绘画、修图、排版与创意工具', '✎', 2, datetime('now')),
('media', '影音', '视频、音乐、播客与媒体服务', '▶', 3, datetime('now')),
('education', '教育', '学习、课程、语言与知识工具', '📚', 4, datetime('now')),
('developer', '开发', '编程、终端、数据库与 DevOps', '⌨', 5, datetime('now')),
('games', '游戏', '订阅、买断与跨区价格对比', '◎', 6, datetime('now')),
('lifestyle', '生活', '健康、天气、旅行与日常工具', '🌿', 7, datetime('now')),
('finance', '财务', '记账、理财、支付与订阅管理', '💰', 8, datetime('now'));

-- 插入初始关注列表数据 (种子 App)
INSERT OR IGNORE INTO watched_app (app_id, name, category_id, enabled, created_at) VALUES 
('425073498', 'Procreate', 'design', 1, datetime('now')),
('1232780281', 'Notion', 'productivity', 1, datetime('now')),
('1062022008', 'LumaFusion', 'media', 1, datetime('now')),
('1444383602', 'GoodNotes 5', 'productivity', 1, datetime('now')),
('1327379505', 'XMind', 'productivity', 1, datetime('now')),
('575816877', 'Todoist', 'productivity', 1, datetime('now'));
