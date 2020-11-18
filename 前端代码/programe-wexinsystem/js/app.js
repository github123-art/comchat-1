window.app = {

	/**
	 *  netty服务后端发布的url
	 */
	nettyserverUrl: 'ws://192.168.43.31:8088/ws',

	/**
	 *   后端服务的url
	 */
	serverUrl: 'http://192.168.43.31:8080',

	/**
	 *   图片服务的url
	 */
	imgServerUrl: "http://192.168.17.129:88/chat/",

	/**
	 * 判断字符串是否为空
	 * @param {Object} str
	 * true = 不为空
	 * false = 空
	 */
	isNotNull: function(str) {
		if (str != "" && str != null && str != undefined) {
			return true;
		}
		return false;

	},

	/**
	 *   提示登录出错消息
	 * @param {Object} msg
	 * @param {Object} type
	 */
	showToast: function(msg, type) {
		plus.nativeUI.toast(msg, {
			icon: "image/" + type + ".png",
			verticalAlign: "center"
		})
	},


	/**
	 *  设置保存全局对象信息
	 * @param {Object} user
	 */
	setUserGlobalInfo: function(user) {
		var userInforStr = JSON.stringify(user);
		plus.storage.setItem("userInfo", userInforStr);
	},

	/**
	 *  获取全局对象的值
	 */
	getUserGlobalInfo: function() {
		var userIntoStr = plus.storage.getItem("userInfo");
		return JSON.parse(userIntoStr);
	},

	/**
	 *   登出后，移除所有全局对象信息
	 */
	userLogout: function() {
		plus.storage.removeItem('userInfo');
	},


	/**
	 *  保存用户的联系人列表
	 * @param {Object} contactList
	 */
	setContactList: function(contactList) {
		var contactListStr = JSON.stringify(contactList);
		plus.storage.setItem('contactList', contactListStr);
	},


	/**
	 *    获取本地缓存中的联系人列表
	 */
	getContactList: function() {
		var contactListStr = plus.storage.getItem('contactList');

		if (!this.isNotNull(contactListStr)) {
			return [];
		}

		return JSON.parse(contactListStr);
	},

	/**
	 *  根据朋友id获取在联系人列表中的用户
	 * @param {Object} friendId
	 */
	getFriendFromContactList: function(friendId) {

		var contactListStr = plus.storage.getItem('contactList');

		//判断联系人列表是否为空
		if (this.isNotNull(contactListStr)) {
			var contactList = JSON.parse(contactListStr);
			for (var i = 0; i < contactList.length; i++) {
				var friend = contactList[i];
				if (friend.friendsUserId == friendId) {
					return friend;
					break;
				}
			}
		} else {
			//如果为空，直接返回null
			return null;
		}
	},

	/**
	 *  标记未读信息为已读信息
	 * 
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	readUserChatSnapshoot: function(myId, friendId) {
		var me = this;
		var chatKey = "chat-Snapshoot" + myId;

		var chatUnSnapshootListStr = plus.storage.getItem(chatKey);
		var chatSnapshootList;
		if (me.isNotNull(chatUnSnapshootListStr)) {

			chatSnapshootList = JSON.parse(chatUnSnapshootListStr);
			//循环该list,对比friendId，如果有，在list中删除原有位置的快照对象，新快照对象替换
			for (var i = 0; i < chatSnapshootList.length; i++) {
				var Item = chatSnapshootList[i];
				if (Item.friendsUserId == friendId) {
					Item.isRead = true; //标记未已读
					chatSnapshootList.splice(i, 1, Item); //替换原有的快照	
					break;
				}
			}
			
			//替换原有的快照列表
			plus.storage.setItem(chatKey,JSON.stringify(chatSnapshootList));
		} else {
			//是空 什么也不做
			return;
		}
	},

	/**
	 *  与后端发送消息动作对应的   枚举类
	 */
	CONNECT: 1, //第一次(或重连)初始化连接
	CHAT: 2, //聊天消息
	SIGNED: 3, //消息签收
	KEEPALIVE: 4, //客户端保持心跳
	PULL_FRIEND: 5, //拉取朋友，使请求发送者更新最小列表


	/**
	 *  保存用户历史聊天记录  作用于当用户再次聊天回到上次聊的话题
	 *   
	 *  保存形式使用 唯一的key 对应聊天记录
	 * 
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} flag  判断本条消息是我发的 还是朋友发的 1.我  2. 朋友
	 */
	saveUserChatHistory: function(myId, friendId, msg, flag) {

		var me = this;
		var chatKey = "chat-" + myId + "-" + friendId;

		//从本地缓存中获取聊天记录是否存在
		var chatHistoryListStr = plus.storage.getItem(chatKey);
		var chatHistoryList;

		if (me.isNotNull(chatHistoryListStr)) {
			//如果不为空
			chatHistoryList = JSON.parse(chatHistoryListStr);
		} else {
			//如果为空，赋一个空list;
			chatHistoryList = [];
		}

		//构建聊天记录对象
		var singleMsg = new me.chatHistory(myId, friendId, msg, flag);

		//向list中追加msg对象
		chatHistoryList.push(singleMsg);

		//向缓存中存储 k-value缓存(历史聊天记录)
		plus.storage.setItem(chatKey, JSON.stringify(chatHistoryList));


	},

	/**
	 *  聊天记录快照，仅仅保存用户之间聊天的最后一条
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead
	 */
	saveUserChatSnapshoot: function(myId, friendId, msg, isRead) {

		var me = this;
		var chatKey = "chat-Snapshoot" + myId;

		//从本地缓存中获取聊天记录是否存在
		var chatSnapshootListStr = plus.storage.getItem(chatKey);
		var chatSnapshootList;

		if (me.isNotNull(chatSnapshootListStr)) {
			//如果不为空
			chatSnapshootList = JSON.parse(chatSnapshootListStr);

			//循环快照list,并且判断每个元素是否包含(匹配)friendId,如果匹配则删除
			for (var i = 0; i < chatSnapshootList.length; i++) {
				if (chatSnapshootList[i].friendId == friendId) {
					//删除已经存在的friendId所对应的快照镜像
					chatSnapshootList.splice(i, 1);
					break;
				}
			}

		} else {
			//如果为空，赋一个空list;
			chatSnapshootList = [];
		};

		//构建聊天快照对象
		var singleSnapshoot = new me.chatSnapshoot(myId, friendId, msg, isRead);

		//向list中追加msg对象
		chatSnapshootList.unshift(singleSnapshoot);

		//向缓存中存储 k-value缓存(历史聊天记录)
		plus.storage.setItem(chatKey, JSON.stringify(chatSnapshootList));


	},

	/**
	 * 
	 *  获取用户快照信息
	 * @param {Object} myId
	 */
	getUserChatSnapshoot: function(myId) {
		var me = this;
		var chatkey = "chat-Snapshoot" + myId;

		//从本地缓存中获取聊天快照的信息
		var chatSnapshootListStr = plus.storage.getItem(chatkey);
		var chatSnapshootList;

		if (me.isNotNull(chatSnapshootListStr)) {
			//如果不为空
			chatSnapshootList = JSON.parse(chatSnapshootListStr);

		} else {
			//如果为空，赋一个空list;
			chatSnapshootList = [];
		}

		return chatSnapshootList;

	},
	
	/**
	 *   删除我与朋友的聊天快照
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	deleteUserChatSnapshoot: function(myId,friendId){
		var me = this;
		var chatKey = "chat-Snapshoot" + myId;
		
		//从本地缓存中获取聊天记录是否存在
		var chatSnapshootListStr = plus.storage.getItem(chatKey);
		var chatSnapshootList;
		
		if (me.isNotNull(chatSnapshootListStr)) {
			//如果不为空
			chatSnapshootList = JSON.parse(chatSnapshootListStr);
		
			//循环快照list,并且判断每个元素是否包含(匹配)friendId,如果匹配则删除
			for (var i = 0; i < chatSnapshootList.length; i++) {
				if (chatSnapshootList[i].friendId == friendId) {
					//删除已经存在的friendId所对应的快照镜像
					chatSnapshootList.splice(i, 1);
					break;
				}
			}
		
		} else {
			//如果为空，赋一个空list;
			chatSnapshootList = [];
		};
		
		//向缓存中存储 k-value缓存(历史聊天记录)
		plus.storage.setItem(chatKey, JSON.stringify(chatSnapshootList));
	},


	/**
	 * 获取用户历史聊天记录
	 * @param {Object} myId
	 * @param {Object} friendId
	 */
	getUserChatHistory: function(myId, friendId) {

		var me = this;
		var chatHistoryList;
		var chatkey = "chat-" + myId + "-" + friendId;
		var chatHistoryListStr = plus.storage.getItem(chatkey);

		if (me.isNotNull(chatHistoryListStr)) {
			chatHistoryList = JSON.parse(chatHistoryListStr);
		} else {
			chatHistoryList = [];
		}
		return chatHistoryList;

	},
	
	/**
	 *   删除我和朋友的本地聊天记录
	 */
	deleteUserChatHistory: function(myId,friendId){
		var chatkey = "chat-" + myId + "-" + friendId;
		plus.storage.removeItem(chatkey);
	},

	/**
	 *  单个历史聊天记录的对象
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} flag
	 */
	chatHistory: function(myId, friendId, msg, flag) {
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.flag = flag;
	},

	/**
	 *  快照对象
	 * @param {Object} myId
	 * @param {Object} friendId
	 * @param {Object} msg
	 * @param {Object} isRead
	 */
	chatSnapshoot: function(myId, friendId, msg, isRead) {
		this.myId = myId;
		this.friendId = friendId;
		this.msg = msg;
		this.isRead = isRead;
	},


	/**
	 *   与后端的ChatMsg 聊天模型对象保持一致
	 * @param {Object} senderId
	 * @param {Object} receiverId
	 * @param {Object} msg
	 * @param {Object} msgId
	 */
	ChanCon: function(senderId, receiverId, msg, msgId) {
		this.senderId = senderId;
		this.receiverId = receiverId;
		this.msg = msg;
		this.msgId = msgId;
	},

	/**
	 *   构建消息 DataContent 对象类型
	 * @param {Object} action
	 * @param {Object} chatCon
	 * @param {Object} extand
	 */
	DataContent: function(action, chatCon, extand) {
		this.action = action;
		this.chatCon = chatCon;
		this.extand = extand;
	}


}
