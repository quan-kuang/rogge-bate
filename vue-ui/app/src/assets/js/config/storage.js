const storage = {};

/* 存储数据*/
storage.setItem = (key, value) => {
    localStorage.setItem(key, JSON.stringify(value));
};

/* 查询数据*/
storage.getItem = (key) => {
    return JSON.parse(localStorage.getItem(key));
};

/* 删除数据*/
storage.removeItem = (key) => {
    localStorage.removeItem(key);
};

export default storage;
