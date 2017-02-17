import sys
import numpy as np
import matplotlib.pyplot as plt
from sklearn.manifold import MDS
from mpl_toolkits.mplot3d import Axes3D
from sklearn.metrics import euclidean_distances

def linear_kernel(x, y):
    return np.dot(x, y)


def polynomial_kernel(x, y, d):
    return (np.dot(x, y) + 1)**d


def get_pol_kernel_closure(d):
    return lambda x,y: polynomial_kernel(x, y, d)


def rbf_kernel(x, y, gamma):
    return np.exp(-gamma * np.dot(x-y, x-y))


def get_rbf_kernel_closure(gamma):
    return lambda x,y: rbf_kernel(x, y, gamma)


def sigmoid_kernel(x, y, a):
    return np.tanh(a * np.dot(x, y) + 1)


def get_sigmoid_kernel_closure(a):
    return lambda x,y: sigmoid_kernel(x, y, a)


def kernel_distance(x, y, kernel_func):
    return np.sqrt(kernel_func(x, x) + kernel_func(y, y) - 2*kernel_func(x, y))


def my_plot2D(corner_points, list_of_pairs, create_figure, title, ax):
    markers = ['o', '^', 's', 'D']
    if create_figure:
        fig = plt.figure()
        ax = fig.add_subplot(111)
    max_x = -np.inf
    min_x = np.inf
    max_y = -np.inf
    min_y = np.inf
    for pair in list_of_pairs:
        ax.plot([pair[0][0], pair[1][0]], [pair[0][1], pair[1][1]], c='b')
        max_x = np.max([max_x, pair[0][0], pair[1][0]])
        min_x = np.min([min_x, pair[0][0], pair[1][0]])
        max_y = np.max([max_y, pair[0][1], pair[1][1]])
        min_y = np.min([min_y, pair[0][1], pair[1][1]])

    for marker, point in zip(markers, corner_points):
        ax.scatter(point[0], point[1], marker=marker, s=100, c='k')

    plt.xlim((min_x - 0.5, max_x + 0.5))
    plt.ylim((min_y - 0.5, max_y + 0.5))
    plt.title(title)


def my_plot3D(corner_points, list_of_pairs, create_figure, title, ax):
    markers = ['o', '^', 's', 'D']
    if create_figure:
        fig = plt.figure()
        ax = fig.add_subplot(111, projection='3d')
    for pair in list_of_pairs:
        ax.plot([pair[0][0], pair[1][0]], [pair[0][1], pair[1][1]], [pair[0][2], pair[1][2]], c='b')

    for marker, point in zip(markers, corner_points):
        ax.scatter(point[0], point[1], point[2], marker=marker, s=100, c='k')

    plt.title(title)


def create_pairs_to_plot_from_grid2D(xx, yy):
    res_list = []
    corner_points = [None]*4

    for i in range(xx.shape[0]):
        for j in range(xx.shape[1]):
            if i != xx.shape[0] - 1:
                res_list.append(((xx[i,j], yy[i,j]),(xx[i+1,j], yy[i+1,j])))
            if j != xx.shape[1] - 1:
                res_list.append(((xx[i, j], yy[i, j]), (xx[i, j+1], yy[i, j+1])))
            if i == 0 and j == 0:
                corner_points[0] = ([xx[i, j], yy[i, j]])
            if i == 0 and j == xx.shape[1] - 1:
                corner_points[1] = ([xx[i, j], yy[i, j]])
            if i == xx.shape[0] - 1 and j == 0:
                corner_points[2] = ([xx[i, j], yy[i, j]])
            if i == xx.shape[0] - 1 and j == xx.shape[1] - 1:
                corner_points[3] = ([xx[i, j], yy[i, j]])

    return corner_points, res_list


# assumption which has to hold is that the order of points in input list corresponds to creation of a distance_matrix
# and to traversing in create_pairs_to_plot_from_grid2D
def create_pairs_to_plot_from_list(input_list, dim_x, dim_y):
    res_list = []
    corner_points = [None]*4

    for i in range(len(input_list)):
        if i % dim_x != dim_x - 1:
            res_list.append((input_list[i],input_list[i+1]))
        if i / dim_x != dim_y - 1:
            res_list.append((input_list[i], input_list[i + dim_x]))
        if i == 0:
            corner_points[0] = (input_list[i])
        if i == dim_x - 1:
            corner_points[1] = (input_list[i])
        if i == len(input_list) - dim_x:
            corner_points[2] = (input_list[i])
        if i == len(input_list) - 1:
            corner_points[3] = (input_list[i])

    return corner_points, res_list


def create_mesh_data(x_min, x_max, x_step, y_min, y_max, y_step):
    x = np.arange(x_min, x_max, x_step)
    y = np.arange(y_min, y_max, y_step)
    return np.meshgrid(x, y)


def get_distance_matrix(xx, yy, kernel_func):
    distance_matrix = np.zeros((xx.shape[0] * xx.shape[1], xx.shape[0] * xx.shape[1]))
    for i1 in range(xx.shape[0]):
        for j1 in range(xx.shape[1]):
            for i2 in range(xx.shape[0]):
                for j2 in range(xx.shape[1]):
                    distance_matrix[i1 * xx.shape[1] + j1, i2 * xx.shape[1] + j2] = \
                        kernel_distance(np.array([xx[i1, j1], yy[i1, j1]]), np.array([xx[i2, j2], yy[i2, j2]]), kernel_func)

    return distance_matrix


def transform_and_plot_data(seed, distance_matrix, dim_x, dim_y, title, plot3D, ax):
    if plot3D:
        n_components = 3
    else:
        n_components = 2
    mds = MDS(n_components=n_components, max_iter=3000, eps=1e-9, random_state=seed, dissimilarity="precomputed", n_jobs=1)
    transformed_data = mds.fit_transform(distance_matrix)

    corner_points, pair_list = create_pairs_to_plot_from_list(transformed_data, dim_x, dim_y)
    if plot3D:
        my_plot3D(corner_points, pair_list, False, title, ax)
    else:
        my_plot2D(corner_points, pair_list, False, title, ax)


def main():
    plot3D = False
    seed = np.random.RandomState(seed=99)
    xx,yy = create_mesh_data(-1, 1.01, 0.2, -1, 1.01, 0.2)

    corner_points, pair_list = create_pairs_to_plot_from_grid2D(xx, yy)
    my_plot2D(corner_points, pair_list, True, 'Original mesh grid', None)

    fig = plt.figure()
    if plot3D:
        ax = fig.add_subplot(111, projection='3d')
    else:
        ax = fig.add_subplot(111)

    distance_matrix = get_distance_matrix(xx, yy, linear_kernel)
    transform_and_plot_data(seed, distance_matrix, xx.shape[1], xx.shape[0], 'Linear kernel', plot3D, ax)

    degrees = [1,2,3,5,10]
    pol_kernels_list = [get_pol_kernel_closure(x) for x in degrees]
    fig = plt.figure()
    for id, kernel in enumerate(pol_kernels_list):
        if plot3D:
            ax = fig.add_subplot(1, len(pol_kernels_list), id + 1, projection='3d')
        else:
            ax = fig.add_subplot(1, len(pol_kernels_list), id + 1)
        distance_matrix = get_distance_matrix(xx, yy, kernel)
        transform_and_plot_data(seed, distance_matrix, xx.shape[1], xx.shape[0], 'Polynomial kernel d={}'.format(degrees[id]),
                                plot3D, ax)

    gammas_list = [0.001, 0.01, 0.5, 1.0, 5.0, 10.0, 20.0]
    rbf_kernels_list = [get_rbf_kernel_closure(x) for x in gammas_list]
    fig = plt.figure()
    for id, kernel in enumerate(rbf_kernels_list):
        if plot3D:
            ax = fig.add_subplot(1, len(rbf_kernels_list), id + 1, projection='3d')
        else:
            ax = fig.add_subplot(1, len(rbf_kernels_list), id + 1)
        distance_matrix = get_distance_matrix(xx, yy, kernel)
        transform_and_plot_data(seed, distance_matrix, xx.shape[1], xx.shape[0], 'RBF kernel gamma={}'.format(gammas_list[id]),
                                plot3D, ax)

    # sig_a_list = [0.001, 0.01, 0.1]
    # sigmoid_kernels_list = [get_sigmoid_kernel_closure(a) for a in sig_a_list]
    # fig = plt.figure()
    # for id, kernel in enumerate(sigmoid_kernels_list):
    #     if plot3D:
    #         ax = fig.add_subplot(1, len(sigmoid_kernels_list), id + 1, projection='3d')
    #     else:
    #         ax = fig.add_subplot(1, len(sigmoid_kernels_list), id + 1)
    #     distance_matrix = get_distance_matrix(xx, yy, kernel)
    #     transform_and_plot_data(seed, distance_matrix, xx.shape[1], xx.shape[0], 'Sigmoidal kernel a={}'.format(sig_a_list[id]),
    #                             plot3D, ax)

    plt.show()


def distance_matrix_visualization():
    xx, yy = create_mesh_data(-1, 1.01, 0.2, -1, 1.01, 0.2)
    points = np.array([[x,y] for x,y in zip(xx.ravel(), yy.ravel())])

    distance_matrix_lin = get_distance_matrix(xx, yy, linear_kernel)
    distance_matrix_pol = get_distance_matrix(xx, yy, get_pol_kernel_closure(10.0))
    distance_matrix_rbf = get_distance_matrix(xx, yy, get_rbf_kernel_closure(10.0))
    distance_matrix_sig = get_distance_matrix(xx, yy, get_sigmoid_kernel_closure(0.1))

    distance_matrix_orig = euclidean_distances(points, points)

    plt.figure()
    plt.pcolor(distance_matrix_orig)
    plt.colorbar()

    plt.figure()
    plt.pcolor(distance_matrix_lin)
    plt.colorbar()

    plt.figure()
    plt.pcolor(distance_matrix_pol)
    plt.colorbar()

    plt.figure()
    plt.pcolor(distance_matrix_rbf)
    plt.colorbar()

    plt.figure()
    plt.pcolor(distance_matrix_sig)
    plt.colorbar()

    plt.show()

if __name__ == "__main__":
    # distance_matrix_visualization()
    main()
