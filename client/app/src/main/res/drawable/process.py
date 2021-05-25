import cv2

img = cv2.imread("loading.png")
for i in range(img.shape[0]):
	for j in range(img.shape[1]):
		for k in range(3):
			img[i][j][k] = min(255, img[i][j][k] + 180)


cv2.imwrite("loading1.png", img);			
			
